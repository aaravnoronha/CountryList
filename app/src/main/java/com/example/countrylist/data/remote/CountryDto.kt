package com.example.countrylist.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryDto(
    @Json(name = "name") val name: String,
    @Json(name = "region") val region: String,
    @Json(name = "code") val code: String,
    @Json(name = "capital") val capital: String
)