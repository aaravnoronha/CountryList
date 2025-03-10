// CountryViewModelTest.kt
package com.example.countrylist.presentation.viewmodel

import app.cash.turbine.test
import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.usecase.GetCountriesUseCase
import com.example.countrylist.util.ResourceState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCountriesUseCase: GetCountriesUseCase
    private lateinit var viewModel: CountryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCountriesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        coEvery { getCountriesUseCase() } returns flowOf(ResourceState.Loading())

        // When
        viewModel = CountryViewModel(getCountriesUseCase)

        // Then
        assertThat(viewModel.uiState.value).isEqualTo(
            CountryState(isLoading = true, countries = emptyList(), error = null)
        )
    }

    @Test
    fun `getCountries updates state with loading and then success`() = runTest {
        // Given
        val mockCountry = Country(
            name = "Test Country",
            capital = "Test Capital",
            code = "TC",
            region = "Test Region"
        )

        coEvery { getCountriesUseCase() } returns flowOf(
            ResourceState.Loading(),
            ResourceState.Success(listOf(mockCountry))
        )

        // When
        viewModel = CountryViewModel(getCountriesUseCase)

        // Then
        viewModel.uiState.test {
            // Initial loading state
            val loadingState = awaitItem()
            assertThat(loadingState).isEqualTo(
                CountryState(isLoading = true, countries = emptyList(), error = null)
            )

            // Success state
            val successState = awaitItem()
            assertThat(successState).isEqualTo(
                CountryState(isLoading = false, countries = listOf(mockCountry), error = null)
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getCountries updates state with loading and then error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getCountriesUseCase() } returns flowOf(
            ResourceState.Loading(),
            ResourceState.Error(errorMessage)
        )

        // When
        viewModel = CountryViewModel(getCountriesUseCase)

        // Then
        viewModel.uiState.test {
            // Initial loading state
            val loadingState = awaitItem()
            assertThat(loadingState).isEqualTo(
                CountryState(isLoading = true, countries = emptyList(), error = null)
            )

            // Error state
            val errorState = awaitItem()
            assertThat(errorState).isEqualTo(
                CountryState(isLoading = false, countries = emptyList(), error = errorMessage)
            )

            cancelAndConsumeRemainingEvents()
        }
    }
}