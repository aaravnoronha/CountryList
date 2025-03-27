// domain/repository/CountryRepository.kt
package com.walmart.countrylist.domain.repository

import com.walmart.countrylist.domain.model.Country
import com.walmart.countrylist.util.ResourceState
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getCountries(forceRefresh: Boolean = false): Flow<ResourceState<List<Country>>>
}