package com.alvindizon.panahon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.ui.locations.LocationForecast
import com.alvindizon.panahon.usecase.GetCoordinatesFromNameUseCase
import com.alvindizon.panahon.usecase.GetForecastForLocationUseCase
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
}

@HiltViewModel
class LocationScreenViewModel @Inject constructor(
    private val getForecastForLocationUseCase: GetForecastForLocationUseCase,
    private val getCoordinatesFromNameUseCase: GetCoordinatesFromNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationScreenUiState>(LocationScreenUiState.Empty)
    val locationScreenUiState: StateFlow<LocationScreenUiState> = _uiState

    fun fetchForecasts() {
        _uiState.value = LocationScreenUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO change this to call from DB
                val locations = LOCATIONS.map {
                    updateForecast(it)
                }
                _uiState.value = LocationScreenUiState.Success(locations)
            } catch (e: Exception) {
                _uiState.value = LocationScreenUiState.Error(e.message ?: e.javaClass.name)
            }
        }
    }

    private suspend fun updateForecast(locationForecast: LocationForecast): LocationForecast {
        val (lat, lon) = getCoordinatesFromNameUseCase.execute(locationForecast.name)
        return getForecastForLocationUseCase.execute(lat, lon)
    }

    companion object {
        private val LOCATIONS = listOf(
            LocationForecast("Singapore", "Clouds", "25"),
            LocationForecast("Jakarta", "Clouds", "28"),
            LocationForecast("Nizhny Novgorod", "Clouds", "28"),
            LocationForecast("Newcastle", "Sunny", "-1"),
            LocationForecast("Manila", "Clouds", "28"),
            LocationForecast("Kyiv", "Clouds", "28"),
        )
    }
}
