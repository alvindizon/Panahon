package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.data.DetailsViewRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchDetailedForecastUseCaseTest {

    private lateinit var useCase: FetchDetailedForecastUseCase

    private val repo: DetailsViewRepository = mockk()

    private val expectedName = "Sydney"

    @BeforeEach
    fun setUp() {
        useCase = FetchDetailedForecastUseCase(repo)
    }

    @Test
    fun `verify that when fetching forecast is successful, usecase returns correct data`() =
        runTest {
            val slot = slot<String>()

            coEvery { repo.fetchDetailedForecast(capture(slot), any(), any(), any()) } returns mockk {
                every { locationName } returns expectedName
            }

            useCase(
                expectedName,
                "-33.865143",
                "151.2099",
                Temperature.Celsius
            ).onCompletion { assertNull(it) }.collectLatest {
                assertEquals(expectedName, it.locationName)
            }

            assertEquals(expectedName, slot.captured)
        }

    @Test
    fun `verify that when fetching forecast errors, usecase should return error as well`() =
        runTest {
            val errorMsg = "error!"
            coEvery { repo.fetchDetailedForecast(any(), any(), any(), any()) } throws Exception(errorMsg)

            useCase(
                expectedName,
                "-33.865143",
                "151.2099",
                Temperature.Celsius
            ).catch {
                assertNotNull(it)
                assertEquals(errorMsg, it.message)
            }
        }
}