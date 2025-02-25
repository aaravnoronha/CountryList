package com.example.countrylist
// Country.kt
data class Country(
    val capital: String,
    val code: String,
    val currency: Currency,
    val flag: String,
    val language: Language,
    val name: String,
    val region: String
)

// Currency.kt
data class Currency(
    val code: String,
    val name: String,
    val symbol: String
)

// Language.kt
data class Language(
    val code: String,
    val name: String
)