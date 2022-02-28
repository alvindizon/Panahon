package com.alvindizon.panahon.data

import com.alvindizon.panahon.data.api.OpenWeatherApi
import com.alvindizon.panahon.data.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.data.api.model.OneCallResponse
import com.alvindizon.panahon.data.api.model.ReverseGeocodeResponseItem
import javax.inject.Inject
import javax.inject.Singleton

interface OpenWeatherRepo {

    suspend fun getWeatherForLocation(latitude: String, longitude: String): OneCallResponse

    suspend fun getLocationNameFromCoordinates(
        latitude: String,
        longitude: String
    ): List<ReverseGeocodeResponseItem>

    suspend fun getCoordinatesFromLocationName(
        location: String,
        limit: String
    ): List<DirectGeocodeResponseItem>
}

@Singleton
class OpenWeatherRepoImpl @Inject constructor(private val api: OpenWeatherApi) : OpenWeatherRepo {
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

    companion object {
        private const val OPENWEATHER_UNIT = "metric"
    }
}
