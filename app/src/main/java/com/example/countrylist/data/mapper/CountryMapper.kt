package com.example.countrylist.data.mapper

import com.example.countrylist.data.local.CountryEntity
import com.example.countrylist.data.remote.CountryDto
import com.example.countrylist.domain.model.Country
import javax.inject.Inject

class CountryMapper @Inject constructor() {
    fun fromEntity(entity: CountryEntity): Country {
        return Country(
            code = entity.code,
            name = entity.name,
            capital = entity.capital,
            region = entity.region
        )
    }

    fun fromDto(dto: CountryDto): Country {
        return Country(
            code = dto.code,
            name = dto.name,
            capital = dto.capital,
            region = dto.region
        )
    }

    fun toEntity(domain: Country): CountryEntity {
        return CountryEntity(
            code = domain.code,
            name = domain.name,
            capital = domain.capital,
            region = domain.region,
            lastUpdated = System.currentTimeMillis()
        )
    }
}