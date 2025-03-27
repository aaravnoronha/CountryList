// domain/model/Country.kt
package com.walmart.countrylist.domain.model

data class Country(
    val code: String,
    val name: String,
    val capital: String,
    val region: String
)