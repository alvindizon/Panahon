package com.alvindizon.panahon.searchlocation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.alvindizon.panahon.data.location.LocationRepository
import com.alvindizon.panahon.data.location.model.CurrentLocation
import com.alvindizon.panahon.data.location.model.LocationResult
import com.alvindizon.panahon.data.location.model.SearchResult
import com.alvindizon.panahon.searchlocation.usecase.SaveLocationUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchLocationViewModelTest {

    private lateinit var viewModel: SearchLocationViewModel

    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)

    private val locationRepository: LocationRepository = mockk()

    private val saveLocationUseCase: SaveLocationUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = SearchLocationViewModel(locationRepository, saveLocationUseCase, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify ui state contains results when repo emits search results`() = runTest {
        val results = listOf(
            SearchResult(
                "Sydney",
                "NSW",
                "AU",
                "-33.865143",
                "151.2099"
            )
        )
        coEvery { locationRepository.searchForLocation(any()) } returns flowOf(results)

        viewModel.onSearchQueryChanged("Sydney")

        advanceUntilIdle()

        assertEquals(results, viewModel.uiState.value.searchResults)
    }

    @Test
    fun `verify ui state contains error message when repo throws Exception`() = runTest {
        val errorMsg = "error!"

        coEvery { locationRepository.searchForLocation(any()) } throws Throwable(errorMsg)

        viewModel.onSearchQueryChanged("Sydney")

        advanceUntilIdle()

        assertEquals(errorMsg, viewModel.uiState.value.errorMessage?.message)
    }

    @Test
    fun `when precise location is enabled, verify locationSettingsNotEnabled is false`() = runTest {
        coEvery { locationRepository.getCurrentLocation() } returns LocationResult(isLocationEnabled = true, currentLocation = null)
        viewModel.onFetchLocationClick()
        advanceUntilIdle()
        assertEquals(false, viewModel.uiState.value.locationSettingsNotEnabled)
    }

    @Test
    fun `when precise location is disabled, verify locationSettingsNotEnabled is true`() = runTest {
        coEvery { locationRepository.getCurrentLocation() } returns LocationResult(isLocationEnabled = false, currentLocation = null)
        viewModel.onFetchLocationClick()
        advanceUntilIdle()
        assert(viewModel.uiState.value.locationSettingsNotEnabled)
    }

    @Test
    fun `when fetched location is null, verify locationNotFound is true`() = runTest {
        coEvery { locationRepository.getCurrentLocation() } returns LocationResult(isLocationEnabled = true, currentLocation = null)
        viewModel.onFetchLocationClick()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.currentLocation)
        assert(viewModel.uiState.value.locationNotFound)
    }

    @Test
    fun `when fetched location is not null, verify UI state has correct current location`() = runTest {
        val expectedLocation = mockk<CurrentLocation> {
            every { locationName } returns "Morphett Vale"
        }
        coEvery { locationRepository.getCurrentLocation() } returns LocationResult(isLocationEnabled = true, currentLocation = expectedLocation)
        viewModel.onFetchLocationClick()
        advanceUntilIdle()
        assertEquals(expectedLocation, viewModel.uiState.value.currentLocation)
        assertEquals(false, viewModel.uiState.value.locationNotFound)
    }

    @Test
    fun `verify error is handled when saving location fails`() = runTest {
        coEvery { saveLocationUseCase.invoke(any(), any(), any()) } throws Exception("bleh")
        viewModel.onSearchResultClick(SearchResult("", "", "", "", ""))
        advanceUntilIdle()
        assertEquals("bleh", viewModel.uiState.value.errorMessage?.message)
    }
}