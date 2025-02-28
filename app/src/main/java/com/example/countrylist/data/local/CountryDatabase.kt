// data/local/CountryDatabase.kt
package com.example.countrylist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CountryEntity::class], version = 1)
abstract class CountryDatabase : RoomDatabase() {
    abstract val countryDao: CountryDao
}