package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.location.LocationManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchCurrentLocationUseCaseTest {

    private val locationManager: LocationManager = mockk()

    private lateinit var useCase: FetchCurrentLocationUseCase

    @BeforeEach
    fun setUp() {
        useCase = FetchCurrentLocationUseCase(locationManager)
    }

    @Test
    fun `when locationManager returns location and name successfully usecase should return CurrentLocation`() = runTest {
        val expectedLocation = CurrentLocation("Sydney", "-33.865143", "151.2099")
        coEvery { locationManager.getCurrentLocation() } returns mockk {
            every { latitude } returns -33.865143
            every { longitude } returns 151.2099
        }
        coEvery { locationManager.getLocationName(any(), any()) } returns "Sydney"

        val result = useCase.execute()
        assertEquals(expectedLocation, result)
    }

    @Test
    fun `when locationManager returns null location and name successfully usecase should return null`() = runTest {
        coEvery { locationManager.getCurrentLocation() } returns null

        val result = useCase.execute()
        assertNull(result)
    }

    @Test
    fun `when locationManager returns location successfully but returns null for name then usecase should return null`() = runTest {
        coEvery { locationManager.getCurrentLocation() } returns mockk {
            every { latitude } returns -33.865143
            every { longitude } returns 151.209900
        }
        coEvery { locationManager.getLocationName(any(), any()) } returns null

        val result = useCase.execute()
        assertNull(result)
    }
}
