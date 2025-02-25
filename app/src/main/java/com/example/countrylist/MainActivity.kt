package com.example.countrylist

// MainActivity.kt
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.hide()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val apiService = ApiService()
                val countries = apiService.getCountries()

                withContext(Dispatchers.Main) {
                    adapter = CountryAdapter(countries.take(10))
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching countries: ${e.message}")
            }
        }
    }
}
