// domain/repository/CountryRepository.kt
package com.example.countrylist.domain.repository

import com.example.countrylist.domain.model.Country
import com.example.countrylist.util.ResourceState
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getCountries(): Flow<ResourceState<List<Country>>>
    suspend fun refreshCountries()
}