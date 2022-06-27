package com.alvindizon.panahon.home.usecase

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.alvindizon.panahon.location.LocationManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckLocationIsOnUseCaseTest {

    private val locationManager: LocationManager = mockk()

    private lateinit var useCase: CheckLocationIsOnUseCase

    private val locationEnableRequest: ActivityResultLauncher<IntentSenderRequest> = mockk()

    @BeforeEach
    fun setUp() {
        useCase = CheckLocationIsOnUseCase(locationManager)
    }

    @Test
    fun `verify usecase returns true when locationmanager returns true`() = runTest {
        coEvery { locationManager.isLocationOn(locationEnableRequest) } returns true
        assert(useCase.execute(locationEnableRequest))
    }

    @Test
    fun `verify usecase returns false when locationmanager returns false`() = runTest {
        coEvery { locationManager.isLocationOn(locationEnableRequest) } returns false
        assertFalse(useCase.execute(locationEnableRequest))
    }
}
