package com.example.countrylist.domain.usecase

import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import com.example.countrylist.util.MainDispatcherRule
import com.example.countrylist.util.ResourceState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCountriesUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CountryRepository
    private lateinit var useCase: GetCountriesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCountriesUseCase(repository)
    }

    @Test
    fun `invokeGetCountriesUseCase emits loading then success when repository returns countries`() = runTest {
        // Given
        val mockCountry = Country(
            name = "Test Country",
            capital = "Test Capital",
            code = "TC",
            region = "Test Region"
        )
        val countryList = listOf(mockCountry)

        coEvery { repository.getCountries() } returns flow {
            emit(ResourceState.Loading)
            emit(ResourceState.Success(countryList))
        }

        // When
        val results = mutableListOf<ResourceState<List<Country>>>()
        useCase.invokeGetCountriesUseCase().toList(results)

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(ResourceState.Loading::class.java)
        assertThat(results[1]).isInstanceOf(ResourceState.Success::class.java)
        assertThat((results[1] as ResourceState.Success).data).containsExactly(mockCountry)
    }

    @Test
    fun `invokeGetCountriesUseCase emits loading then error when repository returns empty list`() = runTest {
        // Given
        coEvery { repository.getCountries() } returns flow {
            emit(ResourceState.Loading)
            emit(ResourceState.Success(emptyList()))
        }

        // When
        val results = mutableListOf<ResourceState<List<Country>>>()
        useCase.invokeGetCountriesUseCase().toList(results)

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(ResourceState.Loading::class.java)
        assertThat(results[1]).isInstanceOf(ResourceState.Error::class.java)
        assertThat((results[1] as ResourceState.Error).error).isEqualTo("No countries found")
    }

    @Test
    fun `invokeGetCountriesUseCase emits error when repository throws exception`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getCountries() } returns flow {
            emit(ResourceState.Loading)
            emit(ResourceState.Error(errorMessage))
        }

        // When
        val results = mutableListOf<ResourceState<List<Country>>>()
        useCase.invokeGetCountriesUseCase().toList(results)

        // Then
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(ResourceState.Loading::class.java)
        assertThat(results[1]).isInstanceOf(ResourceState.Error::class.java)
        assertThat((results[1] as ResourceState.Error).error).isEqualTo(errorMessage)
    }
}