package com.example.countrylist.data.repository

import com.example.countrylist.data.local.CountryDao
import com.example.countrylist.data.mapper.CountryMapper
import com.example.countrylist.data.remote.CountryApi
import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepositoryImpl @Inject constructor(
    private val api: CountryApi,
    private val dao: CountryDao,
    private val mapper: CountryMapper
) : CountryRepository {

    override fun getCountries(): Flow<List<Country>> = flow {
        try {
            val remoteCountries = api.getCountries()
            val countryEntities = remoteCountries.map { dto ->
                mapper.mapDtoToEntity(dto)
            }
            dao.insertAll(countryEntities)

            dao.getAllCountries().collect { entities ->
                emit(entities.map { entity ->
                    mapper.mapEntityToDomain(entity)
                })
            }
        } catch (e: Exception) {
            dao.getAllCountries().collect { entities ->
                emit(entities.map { entity ->
                    mapper.mapEntityToDomain(entity)
                })
            }
        }
    }
}