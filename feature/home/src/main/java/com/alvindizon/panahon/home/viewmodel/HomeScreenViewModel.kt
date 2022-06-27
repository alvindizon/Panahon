package com.alvindizon.panahon.home.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.home.usecase.CheckLocationIsOnUseCase
import com.alvindizon.panahon.home.usecase.CheckPreciseLocationEnabledUseCase
import com.alvindizon.panahon.home.usecase.FetchCurrentLocationUseCase
import com.alvindizon.panahon.home.usecase.GetHomeLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface HomeScreenUiState {
    data class LocationFound(val location: CurrentLocation) : HomeScreenUiState
    object Loading : HomeScreenUiState
    object CheckPreciseLocationEnabled : HomeScreenUiState
    object PreciseLocationEnabled : HomeScreenUiState
    object ShowRationale : HomeScreenUiState
    object LocationOn : HomeScreenUiState
    data class Error(val message: String?) : HomeScreenUiState
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeLocationUseCase: GetHomeLocationUseCase,
    private val checkPreciseLocationEnabledUseCase: CheckPreciseLocationEnabledUseCase,
    private val fetchCurrentLocationUseCase: FetchCurrentLocationUseCase,
    private val checkLocationIsOnUseCase: CheckLocationIsOnUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    fun getHomeLocation() {
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState.Loading
            runCatching {
                getHomeLocationUseCase.execute()
            }.onSuccess {
                if (it != null) {
                    _uiState.value = HomeScreenUiState.LocationFound(it)
                } else {
                    // if no home location in DB check if precise location is enabled
                    _uiState.value = HomeScreenUiState.CheckPreciseLocationEnabled
                }
            }.onFailure {
                _uiState.value = HomeScreenUiState.CheckPreciseLocationEnabled
            }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState.Loading
            runCatching {
                fetchCurrentLocationUseCase.execute()
            }.onSuccess {
                if (it != null) {
                    _uiState.value = HomeScreenUiState.LocationFound(it)
                } else {
                    _uiState.value = HomeScreenUiState.Error(NULL_LOCATION_MSG)
                }
            }.onFailure {
                _uiState.value = HomeScreenUiState.Error(it.message)
            }
        }
    }

    fun checkPreciseLocationEnabled() {
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState.Loading
            runCatching {
                checkPreciseLocationEnabledUseCase.execute()
            }.onSuccess {
                // fetch location latitude and longitude if enabled, else show rationale screen
                if (it) {
                    _uiState.value = HomeScreenUiState.PreciseLocationEnabled
                } else {
                    _uiState.value = HomeScreenUiState.ShowRationale
                }
            }.onFailure {
                _uiState.value = HomeScreenUiState.ShowRationale
            }
        }
    }

    fun isLocationOn(locationEnableRequest: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            runCatching {
                checkLocationIsOnUseCase.execute(locationEnableRequest)
            }.onSuccess {
                if (it) {
                    _uiState.value = HomeScreenUiState.LocationOn
                }
            }
        }
    }

    companion object {
        const val NULL_LOCATION_MSG = "Error: null location"
    }
}
