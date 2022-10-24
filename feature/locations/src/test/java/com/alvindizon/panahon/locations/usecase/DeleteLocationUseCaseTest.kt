package com.alvindizon.panahon.locations.usecase

import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteLocationUseCaseTest {

    private lateinit var useCase: DeleteLocationUseCase

    private val repo: LocationsViewRepository = mockk()

    @BeforeEach
    fun setUp() {
        useCase = DeleteLocationUseCase(repo)
    }

    @Test
    fun `verify that calling execute calls repo`() = runTest {
        val slot = slot<LocationForecast>()
        val locationName = "Sydney"
        val locationForecast: LocationForecast = mockk {
            every { name } returns locationName
        }

        coEvery { repo.deleteLocation(capture(slot)) } just Runs

        useCase.execute(locationForecast)

        assertEquals(locationName, slot.captured.name)

        coVerify(exactly = 1) {
            repo.deleteLocation(any())
        }
    }
}
