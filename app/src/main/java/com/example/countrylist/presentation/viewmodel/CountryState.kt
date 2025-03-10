// presentation/viewmodel/CountryState.kt
package com.example.countrylist.presentation.viewmodel

import com.example.countrylist.domain.model.Country

data class CountryState(
    val isLoading: Boolean = false,
    val countries: List<Country> = emptyList(),
    val error: String? = null
)