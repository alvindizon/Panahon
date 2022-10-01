package com.alvindizon.panahon.integration

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.utils.celsiusToOthers
import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.repo.PanahonRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class LocationsViewRepositoryImpl @Inject constructor(
    private val panahonRepo: PanahonRepo, private val preferencesManager: PreferencesManager
) : LocationsViewRepository {

    override fun fetchSavedLocations(): Flow<List<LocationForecast>> {
        return panahonRepo.fetchSavedLocations().map { locations ->
            locations.map {
                val resp = panahonRepo.getWeatherForLocation(it.latitude, it.longitude)
                val tempUnit = preferencesManager.getTemperatureUnit().first()
                LocationForecast(
                    it.name,
                    it.latitude,
                    it.longitude,
                    resp.current.weather[0].main,
                    resp.current.temp.celsiusToOthers(tempUnit),
                    resp.current.weather[0].icon
                )
            }
        }
    }
}
