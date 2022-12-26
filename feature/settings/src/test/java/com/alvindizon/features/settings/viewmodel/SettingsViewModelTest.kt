package com.alvindizon.features.settings.viewmodel

import com.alvindizon.features.settings.usecase.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

    private val setPreferredUnitUseCase: SetPreferredUnitUseCase = mockk()

    private val fetchPreferredUnitsUseCase: FetchPreferredUnitsUseCase = mockk()

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
            fetchPreferredUnitsUseCase,
            setPreferredUnitUseCase
        )
    }

    @Test
    fun `verify uistate contains cancellation error message when usecase errors`() = runTest {
        coEvery { fetchPreferredUnitsUseCase() } throws Throwable("bleh")
        initViewModel()
        assertEquals("bleh", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `verify uistate contains correct temperature unit index when usecase returns successfully`() =
        runTest {
            coEvery { fetchPreferredUnitsUseCase() } returns flowOf(listOf(1,1,0,1))
            initViewModel()
            assertEquals(1, viewModel.uiState.value.preferredTempUnitIndex)
            assertEquals(1, viewModel.uiState.value.preferredSpeedUnitIndex)
            assertEquals(0, viewModel.uiState.value.preferredPressureUnitIndex)
            assertEquals(1, viewModel.uiState.value.preferredDistanceUnitIndex)
        }

    @Test
    fun `verify uistate isLoading contains correct values as usecase executes`() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        // ref: https://medium.com/androiddevelopers/migrating-to-the-new-coroutines-1-6-test-apis-b99f7fc47774
        Dispatchers.setMain(StandardTestDispatcher())
        coEvery { fetchPreferredUnitsUseCase() } returns flowOf(listOf(1,1,1,1))
        initViewModel()
        assert(viewModel.uiState.value.isLoading)
        // Execute pending coroutine actions
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }
}