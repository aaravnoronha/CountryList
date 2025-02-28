// data/mapper/CountryMapper.kt
package com.example.countrylist.data.mapper

import com.example.countrylist.data.local.CountryEntity
import com.example.countrylist.data.remote.CountryDto
import com.example.countrylist.domain.model.Country
import javax.inject.Inject

class CountryMapper @Inject constructor() {
    fun mapDtoToEntity(dto: CountryDto) = CountryEntity(
        name = dto.name,
        region = dto.region,
        code = dto.code,
        capital = dto.capital
    )

    fun mapEntityToDomain(entity: CountryEntity) = Country(
        name = entity.name,
        region = entity.region,
        code = entity.code,
        capital = entity.capital
    )
}