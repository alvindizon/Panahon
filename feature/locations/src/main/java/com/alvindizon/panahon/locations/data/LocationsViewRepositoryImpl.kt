package com.alvindizon.panahon.locations.data

import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.utils.celsiusToOthers
import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.locations.model.LocationForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationsViewRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val dao: LocationDao,
    private val preferencesManager: PreferencesManager
) : LocationsViewRepository {

    override fun fetchSavedLocations(): Flow<List<LocationForecast>> =
        dao.fetchLocations().map { list ->
            list.map {
                val resp = api.getWeather(it.latitude, it.longitude)
                val tempUnit = preferencesManager.getTemperatureUnit().first()
                LocationForecast(
                    it.name,
                    it.latitude,
                    it.longitude,
                    resp.current.weather[0].main,
                    resp.current.temp.celsiusToOthers(tempUnit),
                    resp.current.weather[0].icon,
                    it.isHomeLocation
                )
            }
        }

    override suspend fun deleteLocation(locationForecast: LocationForecast) =
        dao.delete(locationForecast.name)
}
