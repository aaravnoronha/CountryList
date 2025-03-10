package com.example.countrylist.domain.usecase

import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.repository.CountryRepository
import com.example.countrylist.util.ResourceState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetCountriesUseCaseTest {
    private lateinit var useCase: GetCountriesUseCase
    private lateinit var repository: CountryRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCountriesUseCase(repository)
    }

    @Test
    fun `invoke returns sorted countries on success`() = runTest {
        // Given
        val unsortedCountries = listOf(
            Country("US", "United States", "Washington", "Americas"),
            Country("CA", "Canada", "Ottawa", "Americas"),
            Country("BR", "Brazil", "Brasilia", "Americas")
        )
        every { repository.getCountries() } returns flowOf(ResourceState.Success(unsortedCountries))

        // When
        val result = useCase()

        // Then
        result.collect { state ->
            assertThat(state).isInstanceOf(ResourceState.Success::class.java)
            state as ResourceState.Success
            assertThat(state.data).isInOrder { a, b ->
                (a as Country).name.compareTo((b as Country).name)
            }
        }
    }

    @Test
    fun `invoke propagates error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        every { repository.getCountries() } returns flowOf(ResourceState.Error(errorMessage))

        // When
        val result = useCase()

        // Then
        result.collect { state ->
            assertThat(state).isInstanceOf(ResourceState.Error::class.java)
            state as ResourceState.Error
            assertThat(state.error).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `invoke propagates loading state`() = runTest {
        // Given
        every { repository.getCountries() } returns flowOf(ResourceState.Loading())

        // When
        val result = useCase()

        // Then
        result.collect { state ->
            assertThat(state).isInstanceOf(ResourceState.Loading::class.java)
        }
    }
}