package com.alvindizon.panahon.locations.data

import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.OneCallResponse
import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.db.model.Location
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.model.RawSimpleForecast
import com.alvindizon.panahon.locations.model.SavedLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationsViewRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val dao: LocationDao,
    private val preferencesManager: PreferencesManager
) : LocationsViewRepository {

    override suspend fun fetchSimpleForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): RawSimpleForecast =
        api.getWeather(latitude, longitude).toRawSimpleForecast(locationName)

    override fun fetchTemperatureUnit(): Flow<Temperature> = preferencesManager.getTemperatureUnit()

    override fun fetchSavedLocations(): Flow<List<SavedLocation>> =
        dao.fetchLocations().map { list -> list.map { it.toSavedLocation() } }

    override suspend fun deleteLocation(locationForecast: LocationForecast) =
        dao.delete(locationForecast.name)

    private fun OneCallResponse.toRawSimpleForecast(locationName: String) =
        RawSimpleForecast(
            locationName = locationName,
            currentTemp = current.temp,
            condition = current.weather[0].main,
            icon = current.weather[0].icon
        )

    private fun Location.toSavedLocation() =
        SavedLocation(name, latitude, longitude, isHomeLocation)


}
