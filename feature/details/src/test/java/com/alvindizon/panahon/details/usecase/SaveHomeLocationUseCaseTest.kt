package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.data.location.LocationRepository
import com.alvindizon.panahon.data.location.model.CurrentLocation
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SaveHomeLocationUseCaseTest {

    private lateinit var useCase: SaveHomeLocationUseCase

    private val repo: LocationRepository = mockk()

    private val currentLocation = CurrentLocation(true, "Sydney", "-33.865143", "151.2099")

    private val existingHomeLocation = CurrentLocation(true, "Penrith", "-33.751", "150.692")

    @BeforeEach
    fun setUp() {
        useCase = SaveHomeLocationUseCase(repo)
    }

    @Test
    fun `verify that if home location returned is not null, call update db first and then save current location`() =
        runTest {
            coEvery { repo.getHomeLocation() } returns currentLocation
            coEvery { repo.updateDbLocation(any(), any(), any(), any()) } just Runs
            coEvery { repo.saveLocationToDatabase(any(), any(), any(), any()) } just Runs

            useCase.invoke(
                existingHomeLocation.locationName,
                existingHomeLocation.latitude,
                existingHomeLocation.longitude
            )
            coVerify(exactly = 1) {
                repo.saveLocationToDatabase(
                    existingHomeLocation.locationName,
                    existingHomeLocation.latitude,
                    existingHomeLocation.longitude,
                    true
                )
            }
        }

    @Test
    fun `verify that if there's no currently saved home location then do not update and just save current location`() =
        runTest {
            coEvery { repo.getHomeLocation() } returns null
            coEvery { repo.saveLocationToDatabase(any(), any(), any(), any()) } just Runs

            useCase.invoke(
                existingHomeLocation.locationName,
                existingHomeLocation.latitude,
                existingHomeLocation.longitude
            )

            coVerify(exactly = 0) {
                repo.updateDbLocation(
                    currentLocation.locationName,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    false
                )
            }
            coVerify(exactly = 1) {
                repo.saveLocationToDatabase(
                    existingHomeLocation.locationName,
                    existingHomeLocation.latitude,
                    existingHomeLocation.longitude,
                    true
                )
            }
        }

    @Test
    fun `verify that if existing home location and current location is the same, do not update current home location`() =
        runTest {
            coEvery { repo.getHomeLocation() } returns existingHomeLocation
            coEvery { repo.updateDbLocation(any(), any(), any(), any()) } just Runs
            coEvery { repo.saveLocationToDatabase(any(), any(), any(), any()) } just Runs

            useCase.invoke(
                existingHomeLocation.locationName,
                existingHomeLocation.latitude,
                existingHomeLocation.longitude
            )

            coVerify(exactly = 0) {
                repo.updateDbLocation(
                    currentLocation.locationName,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    false
                )
            }
            coVerify(exactly = 1) {
                repo.saveLocationToDatabase(
                    existingHomeLocation.locationName,
                    existingHomeLocation.latitude,
                    existingHomeLocation.longitude,
                    true
                )
            }
        }
}
