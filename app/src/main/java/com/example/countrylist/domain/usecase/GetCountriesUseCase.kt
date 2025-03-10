// domain/usecase/GetCountriesUseCase.kt
package com.example.countrylist.domain.usecase

import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import com.example.countrylist.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    operator fun invoke(): Flow<ResourceState<List<Country>>> {
        return repository.getCountries()
            .map { resource ->
                when (resource) {
                    is ResourceState.Success -> {
                        val sortedCountries = resource.data.sortedBy { it.name }
                        ResourceState.Success(sortedCountries)
                    }
                    is ResourceState.Error -> resource
                    is ResourceState.Loading -> resource
                }
            }
            .catch { e ->
                emit(ResourceState.Error(e.localizedMessage ?: "An unexpected error occurred"))
            }
    }
}