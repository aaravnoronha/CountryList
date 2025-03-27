package com.walmart.countrylist.data.mapper

import com.walmart.countrylist.data.local.CountryEntity
import com.walmart.countrylist.data.remote.CountryDto
import com.walmart.countrylist.domain.model.Country
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class CountryMapperTest {
    private lateinit var mapper: CountryMapper

    @Before
    fun setup() {
        mapper = CountryMapper()
    }

    @Test
    fun `fromEntity maps CountryEntity to Country correctly`() {
        // Given
        val entity = CountryEntity(
            code = "US",
            name = "United States",
            capital = "Washington",
            region = "Americas",
            lastUpdated = System.currentTimeMillis()
        )

        // When
        val result = mapper.fromEntity(entity)

        // Then
        assertThat(result.code).isEqualTo(entity.code)
        assertThat(result.name).isEqualTo(entity.name)
        assertThat(result.capital).isEqualTo(entity.capital)
        assertThat(result.region).isEqualTo(entity.region)
    }

    @Test
    fun `fromDto maps CountryDto to Country correctly`() {
        // Given
        val dto = CountryDto(
            code = "US",
            name = "United States",
            capital = "Washington",
            region = "Americas"
        )

        // When
        val result = mapper.fromDto(dto)

        // Then
        assertThat(result.code).isEqualTo(dto.code)
        assertThat(result.name).isEqualTo(dto.name)
        assertThat(result.capital).isEqualTo(dto.capital)
        assertThat(result.region).isEqualTo(dto.region)
    }

    @Test
    fun `toEntity maps Country to CountryEntity correctly`() {
        // Given
        val domain = Country(
            code = "US",
            name = "United States",
            capital = "Washington",
            region = "Americas"
        )
        val currentTime = System.currentTimeMillis()

        // When
        val result = mapper.toEntity(domain)

        // Then
        assertThat(result.code).isEqualTo(domain.code)
        assertThat(result.name).isEqualTo(domain.name)
        assertThat(result.capital).isEqualTo(domain.capital)
        assertThat(result.region).isEqualTo(domain.region)
        assertThat(result.lastUpdated).isAtLeast(currentTime)
    }
}