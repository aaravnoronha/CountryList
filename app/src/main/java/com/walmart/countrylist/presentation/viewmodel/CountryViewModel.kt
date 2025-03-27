// presentation/viewmodel/CountryViewModel.kt
package com.walmart.countrylist.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walmart.countrylist.domain.model.Country
import com.walmart.countrylist.domain.usecase.GetCountriesUseCase
import com.walmart.countrylist.util.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {
    companion object {
        val TAG = CountryViewModel::class.java.toString()
    }
    private val _uiState = MutableStateFlow<ResourceState<List<Country>>>(ResourceState.Loading)
    val uiState: StateFlow<ResourceState<List<Country>>> = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            getCountries()
        }
    }
    fun getCountries() {
        getCountriesUseCase.invokeGetCountriesUseCase().onEach { result ->
            _uiState.value = result
        }
            .onStart { Log.d(TAG, "getCountriesUseCase started") }
            .onCompletion { Log.d(TAG,  "getCountriesUseCase complete") }
            .catch { exception -> _uiState.value = ResourceState.Error("Exception: $exception") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = ResourceState.Loading,
            )
            .launchIn(viewModelScope)
    }

}