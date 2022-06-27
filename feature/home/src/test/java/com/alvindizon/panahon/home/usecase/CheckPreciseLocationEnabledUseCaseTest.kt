package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.location.LocationManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckPreciseLocationEnabledUseCaseTest {

    private val locationManager: LocationManager = mockk()

    private lateinit var useCase: CheckPreciseLocationEnabledUseCase

    @BeforeEach
    fun setUp() {
        useCase = CheckPreciseLocationEnabledUseCase(locationManager)
    }

    @Test
    fun `verify usecase returns true if locationManager isPreciseLocationEnabled returns true`() = runTest {
        coEvery { locationManager.isPreciseLocationEnabled() } returns true
        assert(useCase.execute())
    }

    @Test
    fun `verify usecase returns false if locationManager isPreciseLocationEnabled returns false`() = runTest {
        coEvery { locationManager.isPreciseLocationEnabled() } returns false
        assertFalse(useCase.execute())
    }
}
