package com.example.countrylist.data.local

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class LocalDataSourceTest {
    private lateinit var localDataSource: LocalDataSource
    private lateinit var database: CountryDatabase
    private lateinit var dao: CountryDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        database = mockk {
            every { dao } returns this@LocalDataSourceTest.dao
        }
        localDataSource = LocalDataSourceImpl(database)
    }

    @Test
    fun `getCountries returns list from database`() = runTest {
        // Given
        val countries = listOf(
            CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())
        )
        coEvery { dao.getCountries() } returns countries

        // When
        val result = localDataSource.getCountries()

        // Then
        assertThat(result).isEqualTo(countries)
        coVerify { dao.getCountries() }
    }

    @Test
    fun `observeCountries returns flow from database`() = runTest {
        // Given
        val countries = listOf(
            CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())
        )
        every { dao.observeCountries() } returns flowOf(countries)

        // When
        val result = localDataSource.observeCountries()

        // Then
        result.collect {
            assertThat(it).isEqualTo(countries)
        }
    }

    @Test
    fun `shouldUpdateCache returns true when database is empty`() = runTest {
        // Given
        coEvery { dao.getCountryCount() } returns 0

        // When
        val result = localDataSource.shouldUpdateCache()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `shouldUpdateCache returns true when data is old`() = runTest {
        // Given
        val oldTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
        val countries = listOf(
            CountryEntity("US", "United States", "Washington", "Americas", oldTimestamp)
        )
        coEvery { dao.getCountryCount() } returns 1
        coEvery { dao.getCountries() } returns countries

        // When
        val result = localDataSource.shouldUpdateCache()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `shouldUpdateCache returns false when data is fresh`() = runTest {
        // Given
        val freshTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
        val countries = listOf(
            CountryEntity("US", "United States", "Washington", "Americas", freshTimestamp)
        )
        coEvery { dao.getCountryCount() } returns 1
        coEvery { dao.getCountries() } returns countries

        // When
        val result = localDataSource.shouldUpdateCache()

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getCountryByCode returns matching country`() = runTest {
        // Given
        val country = CountryEntity("US", "United States", "Washington", "Americas", System.currentTimeMillis())
        coEvery { dao.getCountryByCode("US") } returns country

        // When
        val result = localDataSource.getCountryByCode("US")

        // Then
        assertThat(result).isEqualTo(country)
    }
}