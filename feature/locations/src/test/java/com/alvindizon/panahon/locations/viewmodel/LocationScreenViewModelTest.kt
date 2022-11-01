package com.alvindizon.panahon.locations.viewmodel

import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.usecase.DeleteLocationUseCase
import com.alvindizon.panahon.locations.usecase.FetchSavedLocationsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LocationScreenViewModelTest {

    private lateinit var viewModel: LocationScreenViewModel

    private val fetchSavedLocationsUseCase: FetchSavedLocationsUseCase = mockk()

    private val deleteLocationUseCase: DeleteLocationUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LocationScreenViewModel(fetchSavedLocationsUseCase, deleteLocationUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify uistate is Success if fetch saved locations returns successfully`() = runTest {
        val locations = listOf(
            LocationForecast("Singapore", "", "", "Clouds", "25", "01d", true),
            LocationForecast("Jakarta", "", "", "Clouds", "28", "01d", false),
            LocationForecast("Nizhny Novgorod", "", "", "Clouds", "28", "01d", false)
        )
        coEvery { fetchSavedLocationsUseCase.execute() } returns locations
        viewModel.fetchForecasts()
        assertEquals(locations, (viewModel.locationScreenUiState.value as LocationScreenUiState.Success).list)
    }

    @Test
    fun `verify uistate is Error if fetch saved locations returns successfully`() = runTest {
        coEvery { fetchSavedLocationsUseCase.execute() } throws Throwable("meh")
        viewModel.fetchForecasts()
        assertEquals("meh", (viewModel.locationScreenUiState.value as LocationScreenUiState.Error).message)
    }

    @Test
    fun `verify uistate isLoading contains correct values as usecase executes`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())
        val locations = listOf(
            LocationForecast("Singapore", "", "", "Clouds", "25", "01d", true),
            LocationForecast("Jakarta", "", "", "Clouds", "28", "01d", false),
            LocationForecast("Nizhny Novgorod", "", "", "Clouds", "28", "01d", false)
        )
        coEvery { fetchSavedLocationsUseCase.execute() } returns locations
        viewModel.fetchForecasts()
        assert(viewModel.locationScreenUiState.value is LocationScreenUiState.Loading)
        // Execute pending coroutine actions
        advanceUntilIdle()
        assertFalse(viewModel.locationScreenUiState.value is LocationScreenUiState.Loading)
    }
}