package com.alvindizon.panahon.locations.usecase

import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchSavedLocationsUseCaseTest {

    private lateinit var useCase: FetchSavedLocationsUseCase

    private val repo: LocationsViewRepository = mockk()

    @BeforeEach
    fun setUp() {
        useCase = FetchSavedLocationsUseCase(repo)
    }

    @Test
    fun `verify that executing usecase returns expected list of locations`() = runTest {
        val locations = listOf(
            LocationForecast("Singapore", "", "", "Clouds", "25", "01d", true),
            LocationForecast("Jakarta", "", "", "Clouds", "28", "01d", false),
            LocationForecast("Nizhny Novgorod", "", "", "Clouds", "28", "01d", false)
        )
        coEvery { repo.fetchSavedLocations() } returns flow {
            emit(locations)
        }
        val result = useCase.execute().first()
        assertEquals(locations, result)
    }
}