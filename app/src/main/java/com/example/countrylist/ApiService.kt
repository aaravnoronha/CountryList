package com.example.countrylist

// ApiService.kt
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson

class ApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getCountries(): List<Country> {
        val request = Request.Builder()
            .url("https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                return gson.fromJson(responseBody, Array<Country>::class.java).toList()
            } else {
                throw Exception("Failed to fetch countries")
            }
        } catch (e: Exception) {
            throw Exception("Error fetching countries: ${e.message}")
        }
    }
}