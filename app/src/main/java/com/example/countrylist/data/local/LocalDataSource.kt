// data/local/LocalDataSource.kt
package com.example.countrylist.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocalDataSource {
    suspend fun getCountries(): List<CountryEntity>
    fun observeCountries(): Flow<List<CountryEntity>>
    suspend fun insertCountries(countries: List<CountryEntity>)
    suspend fun clearCountries()
    suspend fun getCountryByCode(code: String): CountryEntity?
}

class LocalDataSourceImpl @Inject constructor(
    private val database: CountryDatabase
) : LocalDataSource {
    override suspend fun getCountries(): List<CountryEntity> =
        database.dao.getCountries()

    override fun observeCountries(): Flow<List<CountryEntity>> =
        database.dao.observeCountries()

    override suspend fun insertCountries(countries: List<CountryEntity>) =
        database.dao.insertCountries(countries)

    override suspend fun clearCountries() =
        database.dao.deleteAllCountries()

    override suspend fun getCountryByCode(code: String): CountryEntity? =
        database.dao.getCountryByCode(code)
}