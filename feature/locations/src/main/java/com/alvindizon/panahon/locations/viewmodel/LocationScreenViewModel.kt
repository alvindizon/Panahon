package com.alvindizon.panahon.locations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.usecase.DeleteLocationUseCase
import com.alvindizon.panahon.locations.usecase.FetchSavedLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class LocationScreenUiState {
    object Empty : LocationScreenUiState()
    object Loading : LocationScreenUiState()
    class Success(val list: List<LocationForecast>) : LocationScreenUiState()
    class Error(val message: String) : LocationScreenUiState()
    object LocationDeleted: LocationScreenUiState()
}

@HiltViewModel
class LocationScreenViewModel @Inject constructor(
    private val fetchSavedLocationsUseCase: FetchSavedLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationScreenUiState>(LocationScreenUiState.Empty)
    val locationScreenUiState: StateFlow<LocationScreenUiState> = _uiState

    fun fetchForecasts() {
        _uiState.value = LocationScreenUiState.Loading
        viewModelScope.launch {
            runCatching {
                fetchSavedLocationsUseCase.execute()
            }.onSuccess {
                _uiState.value = LocationScreenUiState.Success(it)
            }.onFailure {
                _uiState.value = LocationScreenUiState.Error(it.message ?: it.javaClass.name)
            }
        }
    }

    fun deleteLocation(locationForecast: LocationForecast) {
        _uiState.value = LocationScreenUiState.Loading
        viewModelScope.launch {
            runCatching {
                deleteLocationUseCase.execute(locationForecast)
            }.onSuccess {
                _uiState.value = LocationScreenUiState.LocationDeleted
            }.onFailure {
                _uiState.value = LocationScreenUiState.Error(it.message ?: it.javaClass.name)
            }
        }
    }
}
