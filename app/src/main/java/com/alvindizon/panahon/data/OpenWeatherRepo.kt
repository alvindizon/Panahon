package com.alvindizon.panahon.data

import com.alvindizon.panahon.data.api.OpenWeatherApi
import com.alvindizon.panahon.data.api.model.OneCallResponse
import javax.inject.Inject
import javax.inject.Singleton

interface OpenWeatherRepo {

    suspend fun getWeatherForLocation(latitude: String, longitude: String): OneCallResponse
}

@Singleton
class OpenWeatherRepoImpl @Inject constructor(private val api: OpenWeatherApi) : OpenWeatherRepo {
    override suspend fun getWeatherForLocation(
        latitude: String,
        longitude: String
    ): OneCallResponse = api.getWeather(latitude, longitude, null, OPENWEATHER_UNIT)

    companion object {
        private const val OPENWEATHER_UNIT = "metric"
    }
}
