// domain/usecase/GetCountriesUseCase.kt
package com.example.countrylist.domain.usecase

import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    operator fun invoke(): Flow<List<Country>> {
        return repository.getCountries()
    }
}