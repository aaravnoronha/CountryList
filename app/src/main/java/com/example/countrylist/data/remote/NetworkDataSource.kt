// data/remote/NetworkDataSource.kt
package com.example.countrylist.data.remote

import javax.inject.Inject

interface NetworkDataSource {
    suspend fun getCountries(): List<CountryDto>
}

class NetworkDataSourceImpl @Inject constructor(
    private val api: CountryApi
) : NetworkDataSource {
    override suspend fun getCountries(): List<CountryDto> = api.getCountries()
}