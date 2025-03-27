package com.walmart.countrylist.data.repository

import com.walmart.countrylist.data.local.LocalDataSource
import com.walmart.countrylist.data.mapper.CountryMapper
import com.walmart.countrylist.data.remote.NetworkDataSource
import com.walmart.countrylist.domain.model.Country
import com.walmart.countrylist.domain.repository.CountryRepository
import com.walmart.countrylist.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val mapper: CountryMapper
) : CountryRepository {

    override fun getCountries(forceRefresh: Boolean): Flow<ResourceState<List<Country>>> = flow {
        emit(ResourceState.Loading)
        try {
            if (forceRefresh) {
                emit(fetchFromNetworkAndCache())
            } else {
                val cachedResult = getCountriesFromCache(localDataSource, mapper)
                if (cachedResult.isEmpty()) {
                    emit(fetchFromNetworkAndCache())
                } else {
                    emit(ResourceState.Success(cachedResult))
                    try {
                        val networkResult = getCountriesFromNetwork(networkDataSource, mapper)
                        if (networkResult.isNotEmpty()) {
                            emit(ResourceState.Success(updateCache(localDataSource, mapper, networkResult)))
                        }
                    } catch (e: Exception) {
                        // Ignore network errors when we have cache
                    }
                }
            }
        } catch (e: Exception) {
            val cachedResult = getCountriesFromCache(localDataSource, mapper)
            if (cachedResult.isNotEmpty()) {
                emit(ResourceState.Success(cachedResult))
            } else {
                emit(ResourceState.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }

    private suspend fun fetchFromNetworkAndCache(): ResourceState<List<Country>> {
        val networkResult = getCountriesFromNetwork(networkDataSource, mapper)
        return if (networkResult.isNotEmpty()) {
            ResourceState.Success(updateCache(localDataSource, mapper, networkResult))
        } else {
            val cachedResult = getCountriesFromCache(localDataSource, mapper)
            if (cachedResult.isNotEmpty()) {
                ResourceState.Success(cachedResult)
            } else {
                ResourceState.Error("No data available")
            }
        }
    }

    private suspend fun getCountriesFromCache(
        dataSource: LocalDataSource,
        cMapper: CountryMapper
    ): List<Country> {
        return dataSource.getCountries().map { cMapper.fromEntity(it) }
    }

    private suspend fun getCountriesFromNetwork(
        netwrkDataSource: NetworkDataSource,
        cMapper: CountryMapper
    ): List<Country> {
        val remoteCountries = netwrkDataSource.getCountries()
        return remoteCountries.map { cMapper.fromDto(it) }
    }

    private suspend fun updateCache(
        lSource: LocalDataSource,
        cMapper: CountryMapper,
        domainModels: List<Country>
    ): List<Country> {
        lSource.clearCountries()
        val cachedCountries = domainModels.map { cMapper.toEntity(it) }
        lSource.insertCountries(cachedCountries)
        return domainModels
    }
}