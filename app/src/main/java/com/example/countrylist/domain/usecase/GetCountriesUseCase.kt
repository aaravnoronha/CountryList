// domain/usecase/GetCountriesUseCase.kt
package com.example.countrylist.domain.usecase

import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import com.example.countrylist.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    fun invokeGetCountriesUseCase(): Flow<ResourceState<List<Country>>> = flow {
        emit(ResourceState.Loading)
        repository.getCountries().collect { result ->
            when (result) {
                is ResourceState.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(ResourceState.Success(result.data))
                    } else {
                        emit(ResourceState.Error("No countries found"))
                    }
                }
                is ResourceState.Error -> {
                    emit(ResourceState.Error(result.error))
                }
                is ResourceState.Loading -> {
                    // Don't emit another loading state
                }
            }
        }
    }.catch { e ->
        emit(ResourceState.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }

}
