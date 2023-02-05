package com.alvindizon.panahon.locations.usecase

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.locations.fakes.FakeLocationsRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.model.RawSimpleForecast
import com.alvindizon.panahon.locations.model.SavedLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchLocationsUseCaseTest {

    private lateinit var useCase: FetchLocationsUseCase

    private val repo = FakeLocationsRepository()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        useCase = FetchLocationsUseCase(repo)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify correct forecast values when one of units changes values`() = runTest {
        val forecastValues = mutableListOf<List<LocationForecast>>()
        val locations = listOf(
            SavedLocation("Singapore", "lat", "lon", true),
        )
        val rawForecasts = listOf(
            RawSimpleForecast("Singapore", 25.26, "sunny", "04d"),
        )

        val job = launch(UnconfinedTestDispatcher()) {
            useCase.invoke().toList(forecastValues)
        }
        repo.emitLocations(locations)
        repo.emitTempUnit(Temperature.Celsius)
        repo.emitRawForecast(rawForecasts[0])
        assertNotNull(forecastValues)
        assertEquals(1, forecastValues.size)
        assertEquals(
            LocationForecast(
                "Singapore",
                "lat",
                "lon",
                "sunny", "25°C",
                "04d",
                true
            ),
            forecastValues[0][0]
        )
        job.cancel()
    }
}