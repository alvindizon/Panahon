package com.alvindizon.panahon.data

import com.alvindizon.panahon.data.api.OpenWeatherApi
import com.alvindizon.panahon.data.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.data.api.model.OneCallResponse
import com.alvindizon.panahon.data.api.model.ReverseGeocodeResponseItem
import com.alvindizon.panahon.data.db.LocationDao
import com.alvindizon.panahon.data.db.model.Location
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

    suspend fun saveLocationToDatabase(location: Location)
}

@Singleton
class PanahonRepoImpl @Inject constructor(private val api: OpenWeatherApi, private val dao: LocationDao) : PanahonRepo {
    override suspend fun getWeatherForLocation(
        latitude: String,
        longitude: String
    ): OneCallResponse = api.getWeather(latitude, longitude, null, OPENWEATHER_UNIT)

    override suspend fun getLocationNameFromCoordinates(
        latitude: String,
        longitude: String
    ): List<ReverseGeocodeResponseItem> = api.getLocationName(latitude, longitude)

    override suspend fun getCoordinatesFromLocationName(
        location: String,
        limit: String
    ): List<DirectGeocodeResponseItem> = api.getCities(location, limit)

    override suspend fun saveLocationToDatabase(location: Location) = dao.insert(location)

    companion object {
        private const val OPENWEATHER_UNIT = "metric"
    }
}
