// data/local/CountryEntity.kt
package com.walmart.countrylist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val lastUpdated: Long = System.currentTimeMillis()
)