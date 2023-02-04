package com.alvindizon.panahon.locations.viewmodel

import com.alvindizon.panahon.locations.data.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.usecase.FetchLocationsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LocationScreenViewModelTest {

    private val repository: LocationsViewRepository = mockk()

    private val useCase: FetchLocationsUseCase = mockk()

    private val viewModel: LocationScreenViewModel by lazy {
        LocationScreenViewModel(repository, useCase)
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
    fun `verify uistate contains locations list if fetch saved locations returns successfully`() = runTest {
        every { useCase() } returns flowOf(locations)
        assertEquals(locations, (viewModel.uiState.value.list))
    }

    @Test
    fun `verify uistate contains error message if fetch saved locations errors`() = runTest {
        every { useCase() } throws Exception("error")
        assertEquals("error", (viewModel.uiState.value.errorMessage?.message))
    }

    @Test
    fun `verify uistate isLoading contains correct values as usecase executes`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())
        coEvery { useCase() } returns flow { emit(locations) }
        assert(viewModel.uiState.value.isLoading)
        // Execute pending coroutine actions
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    companion object {
        private val locations = listOf(
            LocationForecast("Singapore", "", "", "Clouds", "25", "01d", true),
            LocationForecast("Jakarta", "", "", "Clouds", "28", "01d", false),
            LocationForecast("Nizhny Novgorod", "", "", "Clouds", "28", "01d", false)
        )
    }
}