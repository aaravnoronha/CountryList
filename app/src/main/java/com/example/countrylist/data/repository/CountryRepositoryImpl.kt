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
        emit(ResourceState.Loading)
        try {
            // Try network first
            val remoteCountries = networkDataSource.getCountries()
            localDataSource.clearCountries()
            localDataSource.insertCountries(remoteCountries.map { mapper.toEntity(it) })
            emit(ResourceState.Success(remoteCountries.map { mapper.fromDto(it) }))
        } catch (e: Exception) {
            // If network fails, try cache
            val cachedCountries = localDataSource.getCountries()
            if (cachedCountries.isNotEmpty()) {
                emit(ResourceState.Success(cachedCountries.map { mapper.fromEntity(it) }))
            } else {
                emit(ResourceState.Error("No local data available"))
            }
        }
    }

    override suspend fun refreshCountries() {
        try {
            val remoteCountries = networkDataSource.getCountries()
            localDataSource.clearCountries()
            localDataSource.insertCountries(remoteCountries.map { mapper.toEntity(it) })
        } catch (e: HttpException) {
            throw IOException("Server error occurred: ${e.localizedMessage ?: "Unknown error"}")
        } catch (e: IOException) {
            throw IOException("Couldn't reach server. Check your internet connection.")
        }
    }
}