package com.example.countrylist.data.repository

import com.example.countrylist.data.local.LocalDataSource
import com.example.countrylist.data.mapper.CountryMapper
import com.example.countrylist.data.remote.NetworkDataSource
import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import com.example.countrylist.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

// data/repository/CountryRepositoryImpl.kt
class CountryRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val mapper: CountryMapper
) : CountryRepository {

    override fun getCountries(forceRefresh: Boolean): Flow<ResourceState<List<Country>>> = flow {
        emit(ResourceState.Loading)
        try {
            if (forceRefresh) {
                try {
                    val remoteCountries = networkDataSource.getCountries()
                    val domainModels = remoteCountries.map { mapper.fromDto(it) }

                    localDataSource.clearCountries()
                    localDataSource.insertCountries(domainModels.map { mapper.toEntity(it) })

                    emit(ResourceState.Success(domainModels))
                    return@flow
                } catch (e: Exception) {
                    // Network failed, fall through to cache
                }
            }

            // Get from cache
            val cachedCountries = localDataSource.getCountries()
            if (cachedCountries.isNotEmpty()) {
                emit(ResourceState.Success(cachedCountries.map { mapper.fromEntity(it) }))
            } else {
                // If cache is empty and we haven't tried network yet, try network
                if (!forceRefresh) {
                    try {
                        val remoteCountries = networkDataSource.getCountries()
                        val domainModels = remoteCountries.map { mapper.fromDto(it) }

                        localDataSource.clearCountries()
                        localDataSource.insertCountries(domainModels.map { mapper.toEntity(it) })

                        emit(ResourceState.Success(domainModels))
                    } catch (e: Exception) {
                        emit(ResourceState.Error("No data available"))
                    }
                } else {
                    emit(ResourceState.Error("No data available"))
                }
            }
        } catch (e: Exception) {
            emit(ResourceState.Error(
                "An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}"
            ))
        }
    }

    override suspend fun refreshCountries() {
        try {
            val remoteCountries = networkDataSource.getCountries()
            val domainModels = remoteCountries.map { mapper.fromDto(it) }

            localDataSource.clearCountries()
            localDataSource.insertCountries(domainModels.map { mapper.toEntity(it) })
        } catch (e: HttpException) {
            throw IOException("Server error occurred: ${e.localizedMessage ?: "Unknown error"}")
        } catch (e: IOException) {
            throw IOException("Couldn't reach server. Check your internet connection.")
        }
    }
}