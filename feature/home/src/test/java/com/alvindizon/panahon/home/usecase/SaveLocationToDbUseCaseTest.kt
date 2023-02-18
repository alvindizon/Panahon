package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.data.HomeRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import io.mockk.*
import kotlinx.coroutines.test.runTest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SaveLocationToDbUseCaseTest {

    private lateinit var useCase: SaveLocationToDbUseCase

    private val repo: HomeRepository = mockk()

    private val currentLocation = CurrentLocation("Sydney", "-33.865143", "151.2099")

    private val existingHomeLocation = CurrentLocation("Penrith", "-33.751", "150.692")

    @BeforeEach
    fun setUp() {
        useCase = SaveLocationToDbUseCase(repo)
    }

    @Test
    fun `verify that if home location returned is not null, call update db first and then save current location`() =
        runTest {
            coEvery { repo.getHomeLocation() } returns currentLocation
            coEvery { repo.updateDbLocation(any(), any(), any(), any()) } just Runs
            coEvery { repo.saveHomeLocationToDatabase(any(), any(), any()) } just Runs

            useCase.execute(
                existingHomeLocation.locationName,
                existingHomeLocation.latitude,
                existingHomeLocation.longitude
            )

            coVerify(exactly = 1) {
                repo.updateDbLocation(
                    currentLocation.locationName,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    false
                )
            }
            coVerify(exactly = 1) {
                repo.saveHomeLocationToDatabase(
                    existingHomeLocation.locationName,
                    existingHomeLocation.latitude,
                    existingHomeLocation.longitude
                )
            }
        }

    @Test
    fun `verify that if there's no currently saved home location then do not update and just save current location`() =
        runTest {
            coEvery { repo.getHomeLocation() } returns null
            coEvery { repo.saveHomeLocationToDatabase(any(), any(), any()) } just Runs

            useCase.execute(
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
                repo.saveHomeLocationToDatabase(
                    existingHomeLocation.locationName,
                    existingHomeLocation.latitude,
                    existingHomeLocation.longitude
                )
            }
        }

    @Test
    fun `verify that if existing home location and current location is the same, do not update current home location`() =
        runTest {
            coEvery { repo.getHomeLocation() } returns existingHomeLocation
            coEvery { repo.updateDbLocation(any(), any(), any(), any()) } just Runs
            coEvery { repo.saveHomeLocationToDatabase(any(), any(), any()) } just Runs

            useCase.execute(
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
                repo.saveHomeLocationToDatabase(
                    existingHomeLocation.locationName,
                    existingHomeLocation.latitude,
                    existingHomeLocation.longitude
                )
            }
        }
}
