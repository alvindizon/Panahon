package com.alvindizon.panahon.locations.usecase

import com.alvindizon.panahon.core.utils.celsiusToOthers
import com.alvindizon.panahon.locations.data.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@ViewModelScoped
class FetchLocationsUseCase @Inject constructor(private val repository: LocationsViewRepository) {

    operator fun invoke(): Flow<List<LocationForecast>> {
        return combine(
            repository.fetchSavedLocations(),
            repository.fetchTemperatureUnit()
        ) { savedLocations, temperatureUnit ->
            savedLocations.map { savedLocation ->
                val simpleForecast = repository.fetchSimpleForecast(
                    savedLocation.locationName,
                    savedLocation.latitude,
                    savedLocation.longitude
                )
                LocationForecast(
                    savedLocation.locationName,
                    savedLocation.latitude,
                    savedLocation.longitude,
                    simpleForecast.condition,
                    simpleForecast.currentTemp.celsiusToOthers(temperatureUnit),
                    simpleForecast.icon,
                    savedLocation.isHomeLocation
                )
            }
        }
    }
}