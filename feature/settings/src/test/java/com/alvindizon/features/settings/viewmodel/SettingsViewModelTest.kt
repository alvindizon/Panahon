package com.alvindizon.features.settings.viewmodel

import com.alvindizon.features.settings.usecase.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

    private val fetchPreferredTempIndexUseCase: FetchPreferredTempIndexUseCase = mockk()

    private val fetchPreferredSpeedIndexUseCase: FetchPreferredSpeedIndexUseCase = mockk()

    private val fetchPreferredPressureIndexUseCase: FetchPreferredPressureIndexUseCase = mockk()

    private val fetchPreferredDistanceIndexUseCase: FetchPreferredDistanceIndexUseCase = mockk()

    private val setPreferredUnitUseCase: SetPreferredUnitUseCase = mockk()

    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        // need to set main dispatcher to prevent
        // "java.lang.IllegalStateException: Module with the Main dispatcher had failed to initialize.
        // For tests Dispatchers.setMain from kotlinx-coroutines-test module can be used"
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun initViewModel() {
        viewModel = SettingsViewModel(
            fetchPreferredTempIndexUseCase,
            fetchPreferredSpeedIndexUseCase,
            fetchPreferredPressureIndexUseCase,
            fetchPreferredDistanceIndexUseCase,
            setPreferredUnitUseCase
        )
    }

    private fun initUseCases() {
        coEvery { fetchPreferredTempIndexUseCase() } returns 1
        coEvery { fetchPreferredSpeedIndexUseCase() } returns 1
        coEvery { fetchPreferredPressureIndexUseCase() } returns 1
        coEvery { fetchPreferredDistanceIndexUseCase() } returns 1
    }

    @Test
    fun `verify uistate contains cancellation error message when usecase errors`() = runTest {
        initUseCases()
        coEvery { fetchPreferredTempIndexUseCase() } throws Throwable("bleh")
        initViewModel()
        assertEquals("An error occurred, please try again", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `verify uistate contains correct temperature unit index when usecase returns successfully`() =
        runTest {
            initUseCases()
            coEvery { fetchPreferredTempIndexUseCase() } returns 1
            initViewModel()
            assertEquals(1, viewModel.uiState.value.preferredTempUnitIndex)
        }

    @Test
    fun `verify uistate isLoading contains correct values as usecase executes`() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        // ref: https://medium.com/androiddevelopers/migrating-to-the-new-coroutines-1-6-test-apis-b99f7fc47774
        Dispatchers.setMain(StandardTestDispatcher())
        coEvery { fetchPreferredTempIndexUseCase() } returns 1
        initViewModel()
        assert(viewModel.uiState.value.isLoading)
        // Execute pending coroutine actions
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }
}