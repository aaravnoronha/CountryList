package com.example.countrylist.domain.repository

import com.example.countrylist.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getCountries(): Flow<List<Country>>
}