// presentation/viewmodel/CountryState.kt
package com.example.countrylist.presentation.viewmodel

import com.example.countrylist.domain.model.Country

data class CountryState(
    val countries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)