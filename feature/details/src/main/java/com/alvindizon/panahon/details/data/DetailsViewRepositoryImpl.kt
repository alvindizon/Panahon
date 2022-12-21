package com.alvindizon.panahon.details.data

import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.Daily
import com.alvindizon.panahon.api.model.Hourly
import com.alvindizon.panahon.api.model.OneCallResponse
import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.*
import com.alvindizon.panahon.core.utils.*
import com.alvindizon.panahon.details.model.DailyForecast
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.HourlyForecast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailsViewRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val preferencesManager: PreferencesManager
) : DetailsViewRepository {

    override suspend fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String,
        temperature: Temperature,
        speed: Speed,
        pressure: Pressure,
        distance: Distance
    ): DetailedForecast {
        return api.getWeather(latitude = latitude, longitude = longitude)
            .toDetailedForecast(locationName, temperature, speed, pressure, distance)
    }

    override fun fetchTemperatureUnit(): Flow<Temperature> = preferencesManager.getTemperatureUnit()

    override fun fetchSpeedUnit(): Flow<Speed> = preferencesManager.getSpeedUnit()

    override fun fetchPressureUnit(): Flow<Pressure> = preferencesManager.getPressureUnit()

    override fun fetchDistanceUnit(): Flow<Distance> = preferencesManager.getDistanceUnit()

    // TODO map OneCallResponse to another model that contains raw values
    private fun OneCallResponse.toDetailedForecast(locationName: String, temperature: Temperature, speed: Speed, pressure: Pressure, distance: Distance) =
        DetailedForecast(
            locationName = locationName,
            sunriseTime = current.sunrise?.toLong()
                ?.convertTimestampToString(EXACT_HOURLY_PATTERN, timezone)
                ?.lowercase(),
            sunsetTime = current.sunset?.toLong()
                ?.convertTimestampToString(EXACT_HOURLY_PATTERN, timezone)
                ?.lowercase(),
            currentTemp = current.temp.celsiusToOthers(temperature),
            feelsLikeTemp = current.feelsLike?.celsiusToOthers(temperature),
            condition = current.weather[0].description,
            icon = current.weather[0].icon,
            hourly = hourly?.take(HOURLY_ITEMS)?.map { it.toHourlyForecast(temperature, timezone) },
            daily = daily?.drop(1)?.map { it.toDailyForecast(temperature, timezone) },
            lastUpdatedTime = current.dt?.toLong()?.convertTimestampToString(COMPLETE_DATE_TIME, null)
                ?: getCurrentTimeString(COMPLETE_DATE_TIME, null),
            windSpeed = current.windSpeed?.msToOthers(speed) ?: "0",
            pressure = current.pressure?.hPaToOthers(pressure) ?: "0",
            visibility = current.visibility?.metersToOthers(distance) ?: "0",
            uvIndex = current.uvi?.toString() ?: "0.0"
        )

    private fun Daily.toDailyForecast(tempUnit: Temperature, timezone: String?): DailyForecast =
        DailyForecast(
            dt?.toLong()?.convertTimestampToString(DAILY_PATTERN, timezone),
            temp?.max?.celsiusToOthers(tempUnit),
            temp?.min?.celsiusToOthers(tempUnit),
            weather?.get(0)?.description,
            weather?.get(0)?.icon,
        )

    private fun Hourly.toHourlyForecast(
        tempUnit: Temperature,
        timezone: String?
    ): HourlyForecast =
        HourlyForecast(
            dt?.toLong()?.convertTimestampToString(HOURLY_PATTERN, timezone)?.lowercase(),
            temp?.toTemperatureString(tempUnit),
            weather?.get(0)?.icon,
        )

    companion object {
        private const val HOURLY_PATTERN = "ha" // example: 5 PM
        private const val EXACT_HOURLY_PATTERN = "h:mm a" // example: 5:55 PM
        private const val DAILY_PATTERN = "EEE d MMM" // example: Thu Jun 9
        private const val COMPLETE_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ssZ"
        private const val HOURLY_ITEMS = 24
    }
}
