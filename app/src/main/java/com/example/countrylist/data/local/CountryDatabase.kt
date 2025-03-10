// data/local/CountryDatabase.kt
package com.example.countrylist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CountryDatabase : RoomDatabase() {
    abstract val dao: CountryDao

    companion object {
        const val DATABASE_NAME = "countries_db"
    }
}