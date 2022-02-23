package com.alvindizon.panahon

import androidx.lifecycle.ViewModel
import com.alvindizon.panahon.ui.locations.LocationForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    fun getLocations(): Flow<List<LocationForecast>> = flow {
        // TODO replace this with call to DB
        emit(LOCATIONS)
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
