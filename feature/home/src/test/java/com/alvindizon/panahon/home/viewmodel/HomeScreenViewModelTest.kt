package com.alvindizon.panahon.home.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.home.usecase.CheckLocationIsOnUseCase
import com.alvindizon.panahon.home.usecase.CheckPreciseLocationEnabledUseCase
import com.alvindizon.panahon.home.usecase.FetchCurrentLocationUseCase
import com.alvindizon.panahon.home.usecase.GetHomeLocationUseCase
import com.alvindizon.panahon.home.viewmodel.HomeScreenViewModel.Companion.NULL_LOCATION_MSG
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HomeScreenViewModelTest {

    private lateinit var viewModel: HomeScreenViewModel

    private val getHomeLocationUseCase: GetHomeLocationUseCase = mockk()

    private val checkPreciseLocationEnabledUseCase: CheckPreciseLocationEnabledUseCase = mockk()

    private val fetchCurrentLocationUseCase: FetchCurrentLocationUseCase = mockk()

    private val checkLocationIsOnUseCase: CheckLocationIsOnUseCase = mockk()

    private val currentLocation: CurrentLocation =
        CurrentLocation("Sydney", "-33.865143", "151.209900")

    private val locationEnableRequest: ActivityResultLauncher<IntentSenderRequest> = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = HomeScreenViewModel(
            getHomeLocationUseCase,
            checkPreciseLocationEnabledUseCase,
            fetchCurrentLocationUseCase,
            checkLocationIsOnUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify uiState is LocationFound if getHomeLocationUseCase returns CurrentLocation`() =
        runTest {
            coEvery { getHomeLocationUseCase.execute() } returns currentLocation
            assert(viewModel.uiState.value is HomeScreenUiState.Loading)
            viewModel.getHomeLocation()
            assert(viewModel.uiState.value is HomeScreenUiState.LocationFound)
            val result = viewModel.uiState.value as HomeScreenUiState.LocationFound
            assertEquals(currentLocation, result.location)
        }

    @Test
    fun `verify uiState is CheckPreciseLocationEnabled if getHomeLocationUseCase returns null`() =
        runTest {
            coEvery { getHomeLocationUseCase.execute() } returns null
            assert(viewModel.uiState.value is HomeScreenUiState.Loading)
            viewModel.getHomeLocation()
            assert(viewModel.uiState.value is HomeScreenUiState.CheckPreciseLocationEnabled)
        }

    @Test
    fun `verify uiState is CheckPreciseLocationEnabled if getHomeLocationUseCase throws Exception`() =
        runTest {
            coEvery { getHomeLocationUseCase.execute() } throws Exception("error!")
            assert(viewModel.uiState.value is HomeScreenUiState.Loading)
            viewModel.getHomeLocation()
            assert(viewModel.uiState.value is HomeScreenUiState.CheckPreciseLocationEnabled)
        }

    @Test
    fun `verify uiState is LocationFound if fetchCurrentLocation returns CurrentLocation`() =
        runTest {
            coEvery { fetchCurrentLocationUseCase.execute() } returns currentLocation
            assert(viewModel.uiState.value is HomeScreenUiState.Loading)
            viewModel.fetchCurrentLocation()
            assert(viewModel.uiState.value is HomeScreenUiState.LocationFound)
            val result = viewModel.uiState.value as HomeScreenUiState.LocationFound
            assertEquals(currentLocation, result.location)
        }

    @Test
    fun `verify uiState is HomeScreenUiState-Error if fetchCurrentLocation returns null`() =
        runTest {
            coEvery { fetchCurrentLocationUseCase.execute() } returns null
            assert(viewModel.uiState.value is HomeScreenUiState.Loading)
            viewModel.fetchCurrentLocation()
            assert(viewModel.uiState.value is HomeScreenUiState.Error)
            val result = viewModel.uiState.value as HomeScreenUiState.Error
            assertEquals(result.message, NULL_LOCATION_MSG)
        }

    @Test
    fun `verify uiState is HomeScreenUiState-Error if fetchCurrentLocation throws Exception`() =
        runTest {
            coEvery { fetchCurrentLocationUseCase.execute() } throws Exception("error!")
            assert(viewModel.uiState.value is HomeScreenUiState.Loading)
            viewModel.fetchCurrentLocation()
            assert(viewModel.uiState.value is HomeScreenUiState.Error)
            val result = viewModel.uiState.value as HomeScreenUiState.Error
            assertEquals(result.message, "error!")
        }

    @Test
    fun `verify uiState is PreciseLocationEnabled when checkPreciseLocationEnabledUseCase returns true`() {
        coEvery { checkPreciseLocationEnabledUseCase.execute() } returns true
        assert(viewModel.uiState.value is HomeScreenUiState.Loading)
        viewModel.checkPreciseLocationEnabled()
        assert(viewModel.uiState.value is HomeScreenUiState.PreciseLocationEnabled)
    }

    @Test
    fun `verify uiState is ShowRationale when checkPreciseLocationEnabledUseCase returns false`() {
        coEvery { checkPreciseLocationEnabledUseCase.execute() } returns false
        assert(viewModel.uiState.value is HomeScreenUiState.Loading)
        viewModel.checkPreciseLocationEnabled()
        assert(viewModel.uiState.value is HomeScreenUiState.ShowRationale)
    }

    @Test
    fun `verify uiState is ShowRationale when checkPreciseLocationEnabledUseCase throws Exception`() {
        coEvery { checkPreciseLocationEnabledUseCase.execute() } throws Exception("error!")
        assert(viewModel.uiState.value is HomeScreenUiState.Loading)
        viewModel.checkPreciseLocationEnabled()
        assert(viewModel.uiState.value is HomeScreenUiState.ShowRationale)
    }

    @Test
    fun `verify uiState is LocationOn when checkLocationIsOnUseCase returns true`() {
        coEvery { checkLocationIsOnUseCase.execute(any()) } returns true
        viewModel.isLocationOn(locationEnableRequest)
        assert(viewModel.uiState.value is HomeScreenUiState.LocationOn)
    }

    @Test
    fun `verify uiState still holds ShowRationale when checkLocationIsOnUseCase returns false`() {
        // simulate that ShowRationale is previous ui state
        coEvery { checkPreciseLocationEnabledUseCase.execute() } returns false
        assert(viewModel.uiState.value is HomeScreenUiState.Loading)
        viewModel.checkPreciseLocationEnabled()
        assert(viewModel.uiState.value is HomeScreenUiState.ShowRationale)

        coEvery { checkLocationIsOnUseCase.execute(any()) } returns false
        viewModel.isLocationOn(locationEnableRequest)
        assert(viewModel.uiState.value is HomeScreenUiState.ShowRationale)
    }

    @Test
    fun `verify uiState still holds ShowRationale when checkLocationIsOnUseCase throws Exception`() {
        // simulate that ShowRationale is previous ui state
        coEvery { checkPreciseLocationEnabledUseCase.execute() } returns false
        assert(viewModel.uiState.value is HomeScreenUiState.Loading)
        viewModel.checkPreciseLocationEnabled()
        assert(viewModel.uiState.value is HomeScreenUiState.ShowRationale)

        coEvery { checkLocationIsOnUseCase.execute(any()) } throws Exception("error!")
        viewModel.isLocationOn(locationEnableRequest)
        assert(viewModel.uiState.value is HomeScreenUiState.ShowRationale)
    }
}
