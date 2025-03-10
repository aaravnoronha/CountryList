// presentation/viewmodel/CountryViewModel.kt
package com.example.countrylist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countrylist.domain.usecase.GetCountriesUseCase
import com.example.countrylist.util.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountryState())
    val uiState: StateFlow<CountryState> = _uiState.asStateFlow()

    init {
        getCountries()
    }

    private fun getCountries() {
        getCountriesUseCase().onEach { result ->
            when (result) {
                is ResourceState.Loading -> {
                    _uiState.value = CountryState(isLoading = true)
                }
                is ResourceState.Success -> {
                    _uiState.value = CountryState(
                        countries = result.data
                    )
                }
                is ResourceState.Error -> {
                    _uiState.value = CountryState(
                        error = result.error
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}