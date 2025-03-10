// data/remote/CountryDto.kt
package com.example.countrylist.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    @SerialName("code")
    val code: String,
    @SerialName("name")
    val name: String,
    @SerialName("capital")
    val capital: String,
    @SerialName("region")
    val region: String
)