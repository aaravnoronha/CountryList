package com.walmart.countrylist.presentation.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.walmart.countrylist.databinding.CountryItemBinding
import com.walmart.countrylist.domain.model.Country

class CountryAdapter : ListAdapter<Country, CountryAdapter.ViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CountryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(country: Country) {
            binding.apply {
                nameRegionTextView.text = "${country.name}, ${country.region}"
                codeTextView.text = country.code
                capitalTextView.text = country.capital
            }
        }
    }

    private class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }
}