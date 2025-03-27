package com.walmart.countrylist.data.repository

import com.walmart.countrylist.data.local.CountryEntity
import com.walmart.countrylist.data.local.LocalDataSource
import com.walmart.countrylist.data.mapper.CountryMapper
import com.walmart.countrylist.data.remote.CountryDto
import com.walmart.countrylist.data.remote.NetworkDataSource
import com.walmart.countrylist.domain.model.Country
import com.walmart.countrylist.util.ResourceState
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

    private val testDto = CountryDto("US", "United States", "Washington", "Americas")
    private val testCountry = Country("US", "United States", "Washington", "Americas")
    private val testEntity = CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())

    @Before
    fun setup() {
        networkDataSource = mockk()
        localDataSource = mockk()
        mapper = mockk()
        repository = CountryRepositoryImpl(networkDataSource, localDataSource, mapper)
    }

    @Test
    fun `getCountries with forceRefresh true fetches from network and updates cache`() = runTest {
        // Given
        setupSuccessfulMocks()

        // When
        val result = repository.getCountries(forceRefresh = true)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Success -> {
                    assertThat(state.data).containsExactly(testCountry)
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }

        verifyNetworkAndCacheOperations()
    }


    @Test
    fun `getCountries with empty cache falls back to network`() = runTest {
        // Given
        coEvery { localDataSource.getCountries() } returns emptyList()
        setupSuccessfulMocks()

        // When
        val result = repository.getCountries(forceRefresh = false)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Success -> {
                    assertThat(state.data).containsExactly(testCountry)
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }

        verifyNetworkAndCacheOperations()
    }

    @Test
    fun `getCountries with network error falls back to cache`() = runTest {
        // Given
        coEvery { networkDataSource.getCountries() } throws IOException()
        coEvery { localDataSource.getCountries() } returns listOf(testEntity)
        every { mapper.fromEntity(testEntity) } returns testCountry

        // When
        val result = repository.getCountries(forceRefresh = true)

        // Then
        result.collect { state ->
            when (state) {
                is ResourceState.Success -> {
                    assertThat(state.data).containsExactly(testCountry)
                }
                else -> assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
            }
        }
    }

    private fun setupSuccessfulMocks() {
        coEvery { networkDataSource.getCountries() } returns listOf(testDto)
        every { mapper.fromDto(testDto) } returns testCountry
        every { mapper.toEntity(testCountry) } returns testEntity
        coEvery { localDataSource.clearCountries() } returns Unit
        coEvery { localDataSource.insertCountries(any()) } returns Unit
    }

    private fun verifyNetworkAndCacheOperations() {
        coVerify {
            networkDataSource.getCountries()
            localDataSource.clearCountries()
            localDataSource.insertCountries(any())
        }
    }
}