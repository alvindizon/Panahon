package com.alvindizon.panahon.data

import com.alvindizon.panahon.data.api.OpenWeatherApi
import com.alvindizon.panahon.data.api.model.DirectGeocodeResponse
import com.alvindizon.panahon.data.api.model.OneCallResponse
import com.alvindizon.panahon.data.api.model.ReverseGeocodeResponse
import javax.inject.Inject
import javax.inject.Singleton

interface OpenWeatherRepo {

    suspend fun getWeatherForLocation(latitude: String, longitude: String): OneCallResponse

    suspend fun getLocationNameFromCoordinates(
        latitude: String,
        longitude: String
    ): ReverseGeocodeResponse

    suspend fun getCoordinatesFromLocationName(
        location: String,
        limit: String
    ): DirectGeocodeResponse
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
    ): ReverseGeocodeResponse = api.getLocationName(latitude, longitude)

    override suspend fun getCoordinatesFromLocationName(
        location: String,
        limit: String
    ): DirectGeocodeResponse = api.getCities(location, limit)

    companion object {
        private const val OPENWEATHER_UNIT = "metric"
    }
}
