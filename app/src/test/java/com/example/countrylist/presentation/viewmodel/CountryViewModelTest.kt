// CountryViewModelTest.kt
package com.example.countrylist.presentation.viewmodel

import app.cash.turbine.test
import com.example.countrylist.domain.model.Country
import com.example.countrylist.domain.usecase.GetCountriesUseCase
import com.example.countrylist.util.MainDispatcherRule
import com.example.countrylist.util.MockLogRule
import com.example.countrylist.util.ResourceState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val mockLogRule = MockLogRule()

    private lateinit var getCountriesUseCase: GetCountriesUseCase
    private lateinit var viewModel: CountryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        getCountriesUseCase = mockk()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        coEvery { getCountriesUseCase.invokeGetCountriesUseCase() } returns flow {
            emit(ResourceState.Loading)
        }

        // When
        viewModel = CountryViewModel(getCountriesUseCase)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value).isEqualTo(ResourceState.Loading)
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

        coEvery { getCountriesUseCase.invokeGetCountriesUseCase() } returns flow {
            emit(ResourceState.Loading)
            emit(ResourceState.Success(listOf(mockCountry)))
        }

        // When
        viewModel = CountryViewModel(getCountriesUseCase)

        // Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ResourceState.Loading)
            assertThat(awaitItem()).isEqualTo(ResourceState.Success(listOf(mockCountry)))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getCountries updates state with loading and then error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getCountriesUseCase.invokeGetCountriesUseCase() } returns flow {
            emit(ResourceState.Loading)
            throw Exception(errorMessage)
        }

        // When
        viewModel = CountryViewModel(getCountriesUseCase)

        // Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ResourceState.Loading)
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(ResourceState.Error::class.java)
            assertThat((errorState as ResourceState.Error).error).contains(errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }
}