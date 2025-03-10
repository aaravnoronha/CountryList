// data/repository/CountryRepositoryImpl.kt
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

class CountryRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val mapper: CountryMapper
) : CountryRepository {

    override fun getCountries(): Flow<ResourceState<List<Country>>> = flow {
        emit(ResourceState.Loading())

        try {
            // First emit cached data
            val cachedCountries = localDataSource.getCountries()
            emit(ResourceState.Success(cachedCountries.map { mapper.fromEntity(it) }))

            // Check if we should update cache
            if (localDataSource.shouldUpdateCache()) {
                try {
                    val remoteCountries = networkDataSource.getCountries()
                    localDataSource.clearCountries()
                    localDataSource.insertCountries(remoteCountries.map { mapper.toEntity(it) })
                    emit(ResourceState.Success(remoteCountries.map { mapper.fromDto(it) }))
                } catch (e: HttpException) {
                    emit(ResourceState.Error(
                        "Server error occurred: ${e.localizedMessage ?: "Unknown error"}"
                    ))
                } catch (e: IOException) {
                    emit(ResourceState.Error(
                        "Couldn't reach server. Check your internet connection."
                    ))
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
            localDataSource.clearCountries()
            localDataSource.insertCountries(remoteCountries.map { mapper.toEntity(it) })
        } catch (e: Exception) {
            throw e
        }
    }
}