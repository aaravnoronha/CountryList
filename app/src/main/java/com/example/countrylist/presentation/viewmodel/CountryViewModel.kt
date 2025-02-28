// presentation/viewmodel/CountryViewModel.kt
package com.example.countrylist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countrylist.domain.usecase.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CountryState())
    val state: StateFlow<CountryState> = _state

    init {
        getCountries()
    }

    private fun getCountries() {
        getCountriesUseCase()
            .onEach { countries ->
                _state.value = CountryState(
                    countries = countries,
                    isLoading = false
                )
            }
            .catch { e ->
                _state.value = CountryState(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }
}