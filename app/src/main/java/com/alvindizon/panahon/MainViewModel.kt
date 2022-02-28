package com.alvindizon.panahon

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


sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    class Success(val list: List<LocationForecast>) : UiState()
    class Error(val message: String) : UiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getForecastForLocationUseCase: GetForecastForLocationUseCase,
    private val getCoordinatesFromNameUseCase: GetCoordinatesFromNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState

    fun fetchForecasts() {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO change this to call from DB
                val locations = LOCATIONS.map {
                    updateForecast(it)
                }
                _uiState.value = UiState.Success(locations)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: e.javaClass.name)
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
