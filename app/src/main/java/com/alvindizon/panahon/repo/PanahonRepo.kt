package com.alvindizon.panahon.repo

import com.alvindizon.panahon.BuildConfig
import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.api.model.OneCallResponse
import com.alvindizon.panahon.api.model.ReverseGeocodeResponseItem
import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.db.model.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PanahonRepo {

    suspend fun getWeatherForLocation(latitude: String, longitude: String): OneCallResponse

    suspend fun getLocationNameFromCoordinates(
        latitude: String,
        longitude: String
    ): List<ReverseGeocodeResponseItem>

    suspend fun getCoordinatesFromLocationName(
        location: String,
        limit: String
    ): List<DirectGeocodeResponseItem>

    suspend fun saveLocationToDatabase(name: String, latitude: String, longitude: String)

    fun fetchSavedLocations(): Flow<List<Location>>
}

@Singleton
class PanahonRepoImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val dao: LocationDao
) : PanahonRepo {
    override suspend fun getWeatherForLocation(
        latitude: String,
        longitude: String
    ): OneCallResponse =
        api.getWeather(latitude, longitude, null, OPENWEATHER_UNIT, BuildConfig.OPENWEATHER_KEY)

    override suspend fun getLocationNameFromCoordinates(
        latitude: String,
        longitude: String
    ): List<ReverseGeocodeResponseItem> =
        api.getLocationName(latitude, longitude, BuildConfig.OPENWEATHER_KEY)

    override suspend fun getCoordinatesFromLocationName(
        location: String,
        limit: String
    ): List<DirectGeocodeResponseItem> = api.getCities(location, limit, BuildConfig.OPENWEATHER_KEY)

    override suspend fun saveLocationToDatabase(name: String, latitude: String, longitude: String) =
        dao.insert(Location(name, latitude, longitude, false))

    override fun fetchSavedLocations(): Flow<List<Location>> = dao.fetchLocations()

    companion object {
        private const val OPENWEATHER_UNIT = "metric"
    }
}
