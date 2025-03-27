// data/local/CountryDao.kt
package com.walmart.countrylist.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries")
    suspend fun getCountries(): List<CountryEntity>

    @Query("SELECT * FROM countries")
    fun observeCountries(): Flow<List<CountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Query("DELETE FROM countries")
    suspend fun deleteAllCountries()

    @Query("SELECT * FROM countries WHERE code = :code")
    suspend fun getCountryByCode(code: String): CountryEntity?

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCountryCount(): Int
}