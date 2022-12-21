package com.alvindizon.panahon.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.usecase.FetchDetailedForecastUseCase
import com.alvindizon.panahon.details.usecase.FetchTemperatureUnitUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class DetailsScreenViewModelTest {

    private val fetchTemperatureUnitUseCase: FetchTemperatureUnitUseCase = mockk()

    private val fetchDetailedForecastUseCase: FetchDetailedForecastUseCase = mockk()

    private val savedStateHandle: SavedStateHandle = mockk()

    private val viewModel: DetailsScreenViewModel by lazy {
        DetailsScreenViewModel(
            fetchDetailedForecastUseCase,
            fetchTemperatureUnitUseCase,
            savedStateHandle
        )
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify uistate contains correct error message when fetch temperature unit usecase errors`() =
        runTest {
            coEvery { savedStateHandle.get<String>(any()) } returns ""
            coEvery { fetchTemperatureUnitUseCase() } throws Throwable("bleh")
            coEvery {
                fetchDetailedForecastUseCase(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns mockk()
            assertEquals("bleh", viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `verify uistate contains correct error message when fetch detailed forecast usecase errors`() =
        runTest {
            coEvery { savedStateHandle.get<String>(any()) } returns ""
            coEvery { fetchTemperatureUnitUseCase() } returns flow { emit(Temperature.Celsius) }
            coEvery {
                fetchDetailedForecastUseCase(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } throws Throwable("bleh")
            assertEquals("bleh", viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `verify uistate contains correct detailed forecast when fetch forecast and fetch temp unit usecase succeeds`() =
        runTest {
            val mockForecast = mockk<DetailedForecast> {
                every { locationName } returns "Sydney"
            }
            coEvery { savedStateHandle.get<String>(any()) } returns ""
            coEvery { fetchTemperatureUnitUseCase() } returns flow { emit(Temperature.Celsius) }
            coEvery {
                fetchDetailedForecastUseCase(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns flow { emit(mockForecast) }

            assertEquals("Sydney", viewModel.uiState.value.detailedForecast?.locationName)
        }
}