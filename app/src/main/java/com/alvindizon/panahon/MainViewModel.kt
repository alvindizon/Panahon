package com.alvindizon.panahon

import androidx.lifecycle.ViewModel
import com.alvindizon.panahon.ui.locations.LocationForecast
import com.alvindizon.panahon.usecase.GetCoordinatesFromNameUseCase
import com.alvindizon.panahon.usecase.GetForecastForLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getForecastForLocationUseCase: GetForecastForLocationUseCase,
    private val getCoordinatesFromNameUseCase: GetCoordinatesFromNameUseCase
) : ViewModel() {

    val itemsFlow = flow {
        // TODO change this to call from DB
        val locations = LOCATIONS.map {
            updateForecast(it)
        }
        emit(locations)
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
