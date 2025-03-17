package com.example.countrylist.data.repository

import com.example.countrylist.data.local.CountryEntity
import com.example.countrylist.data.local.LocalDataSource
import com.example.countrylist.data.mapper.CountryMapper
import com.example.countrylist.data.remote.CountryDto
import com.example.countrylist.data.remote.NetworkDataSource
import com.example.countrylist.domain.model.Country
import com.example.countrylist.util.ResourceState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class CountryRepositoryImplTest {
    private lateinit var repository: CountryRepositoryImpl
    private lateinit var networkDataSource: NetworkDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var mapper: CountryMapper

    @Before
    fun setup() {
        networkDataSource = mockk()
        localDataSource = mockk()
        mapper = mockk()
        repository = CountryRepositoryImpl(networkDataSource, localDataSource, mapper)
    }

    @Test
    fun `getCountries with forceRefresh true fetches from network`() = runTest {
        // Given
        val dto = CountryDto("US", "United States", "Washington", "Americas")
        val country = Country("US", "United States", "Washington", "Americas")
        val entity = CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())

        coEvery { networkDataSource.getCountries() } returns listOf(dto)
        every { mapper.fromDto(dto) } returns country
        every { mapper.toEntity(country) } returns entity
        coEvery { localDataSource.clearCountries() } returns Unit
        coEvery { localDataSource.insertCountries(any()) } returns Unit

        // When
        val result = repository.getCountries(forceRefresh = true)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Success -> {
                    assertThat(state.data).containsExactly(country)
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }

        coVerify {
            networkDataSource.getCountries()
            localDataSource.clearCountries()
            localDataSource.insertCountries(any())
        }
    }

    @Test
    fun `getCountries with forceRefresh false returns cached data when available`() = runTest {
        // Given
        val entity = CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())
        val country = Country("US", "United States", "Washington", "Americas")
        coEvery { localDataSource.getCountries() } returns listOf(entity)
        every { mapper.fromEntity(entity) } returns country

        // When
        val result = repository.getCountries(forceRefresh = false)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Success -> {
                    assertThat(state.data).containsExactly(country)
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }

        coVerify(exactly = 0) { networkDataSource.getCountries() }
    }

    @Test
    fun `getCountries with empty cache falls back to network`() = runTest {
        // Given
        val dto = CountryDto("US", "United States", "Washington", "Americas")
        val country = Country("US", "United States", "Washington", "Americas")
        val entity = CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())

        coEvery { localDataSource.getCountries() } returns emptyList()
        coEvery { networkDataSource.getCountries() } returns listOf(dto)
        every { mapper.fromDto(dto) } returns country
        every { mapper.toEntity(country) } returns entity
        coEvery { localDataSource.clearCountries() } returns Unit
        coEvery { localDataSource.insertCountries(any()) } returns Unit

        // When
        val result = repository.getCountries(forceRefresh = false)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Success -> {
                    assertThat(state.data).containsExactly(country)
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }
    }

    @Test
    fun `getCountries returns error when both network and cache fail`() = runTest {
        // Given
        coEvery { localDataSource.getCountries() } returns emptyList()
        coEvery { networkDataSource.getCountries() } throws IOException()

        // When
        val result = repository.getCountries(forceRefresh = false)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Error -> {
                    assertThat(state.error).contains("No data available")
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }
    }

    @Test
    fun `refreshCountries updates cache with network data`() = runTest {
        // Given
        val dto = CountryDto("US", "United States", "Washington", "Americas")
        val country = Country("US", "United States", "Washington", "Americas")
        val entity = CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())

        coEvery { networkDataSource.getCountries() } returns listOf(dto)
        every { mapper.fromDto(dto) } returns country
        every { mapper.toEntity(country) } returns entity
        coEvery { localDataSource.clearCountries() } returns Unit
        coEvery { localDataSource.insertCountries(any()) } returns Unit

        // When
        repository.refreshCountries()

        // Then
        coVerify {
            networkDataSource.getCountries()
            localDataSource.clearCountries()
            localDataSource.insertCountries(any())
        }
    }

    @Test
    fun `refreshCountries throws IOException on network failure`() = runTest {
        // Given
        coEvery { networkDataSource.getCountries() } throws IOException()

        // When/Then
        try {
            repository.refreshCountries()
            assertThat(false).isTrue() // Should not reach here
        } catch (e: IOException) {
            assertThat(e.message).contains("Couldn't reach server")
        }
    }
}