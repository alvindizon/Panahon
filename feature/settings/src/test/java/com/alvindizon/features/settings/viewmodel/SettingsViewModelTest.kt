package com.alvindizon.features.settings.viewmodel

import com.alvindizon.features.settings.usecase.FetchPreferredTempIndexUseCase
import com.alvindizon.features.settings.usecase.SetPreferredTempUnitUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

    private val fetchPreferredTempIndexUseCase: FetchPreferredTempIndexUseCase = mockk()

    private val setPreferredTempUnitUseCase: SetPreferredTempUnitUseCase = mockk()

    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        // need to set main dispatcher to prevent
        // "java.lang.IllegalStateException: Module with the Main dispatcher had failed to initialize.
        // For tests Dispatchers.setMain from kotlinx-coroutines-test module can be used"
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = SettingsViewModel(fetchPreferredTempIndexUseCase, setPreferredTempUnitUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify uistate contains correct error message when usecase errors`() = runTest {
        coEvery { fetchPreferredTempIndexUseCase.execute() } throws Throwable("bleh")
        viewModel.fetchPreferredTemperatureUnit()
        assertEquals("bleh", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `verify uistate contains correct temperature unit index when usecase returns successfully`() = runTest {
        coEvery { fetchPreferredTempIndexUseCase.execute() } returns 1
        viewModel.fetchPreferredTemperatureUnit()
        assertEquals(1, viewModel.uiState.value.preferredTempUnitIndex)
    }

    @Test
    fun `verify uistate isLoading contains correct values as usecase executes`() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        // ref: https://medium.com/androiddevelopers/migrating-to-the-new-coroutines-1-6-test-apis-b99f7fc47774
        Dispatchers.setMain(StandardTestDispatcher())
        coEvery { fetchPreferredTempIndexUseCase.execute() } returns 1
        viewModel.fetchPreferredTemperatureUnit()
        assert(viewModel.uiState.value.isLoading)
        // Execute pending coroutine actions
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }
}