package com.example.countrylist

// CountryAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CountryAdapter(private val countries: List<Country>) :
    RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameRegionTextView: TextView = view.findViewById(R.id.name_region_text_view)
        val codeTextView: TextView = view.findViewById(R.id.code_text_view)
        val capitalTextView: TextView = view.findViewById(R.id.capital_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.country_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.nameRegionTextView.text = "${country.name}, ${country.region}"
        holder.codeTextView.text = country.code
        holder.capitalTextView.text = country.capital
    }

    override fun getItemCount() = countries.size
}