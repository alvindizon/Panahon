package com.alvindizon.panahon.details.data

import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.Daily
import com.alvindizon.panahon.api.model.Hourly
import com.alvindizon.panahon.api.model.OneCallResponse
import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.core.utils.DateTimeUtils
import com.alvindizon.panahon.details.model.RawDaily
import com.alvindizon.panahon.details.model.RawDetailedForecast
import com.alvindizon.panahon.details.model.RawHourly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailsViewRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val preferencesManager: PreferencesManager
) : DetailsViewRepository {

    override fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): Flow<RawDetailedForecast> =
        flow { emit(api.getWeather(latitude, longitude).toRawDetailedForecast(locationName)) }

    override fun fetchTemperatureUnit(): Flow<Temperature> = preferencesManager.getTemperatureUnit()

    override fun fetchSpeedUnit(): Flow<Speed> = preferencesManager.getSpeedUnit()

    override fun fetchPressureUnit(): Flow<Pressure> = preferencesManager.getPressureUnit()

    override fun fetchDistanceUnit(): Flow<Distance> = preferencesManager.getDistanceUnit()

    private fun OneCallResponse.toRawDetailedForecast(locationName: String) =
        RawDetailedForecast(
            locationName = locationName,
            timezone = timezone,
            sunriseTime = current.sunrise,
            sunsetTime = current.sunset,
            currentTemp = current.temp,
            feelsLikeTemp = current.feelsLike,
            condition = current.weather[0].description,
            icon = current.weather[0].icon,
            hourly = hourly?.take(DateTimeUtils.HOURLY_ITEMS)?.map { it.toRawHourly() },
            daily = daily?.drop(1)?.map { it.toRawDaily() },
            lastUpdatedTime = current.dt,
            windSpeed = current.windSpeed,
            pressure = current.pressure,
            visibility = current.visibility,
            uvIndex = current.uvi
        )

    private fun Daily.toRawDaily(): RawDaily =
        RawDaily(
            date = dt,
            maximumTemp = temp?.max,
            minimumTemp = temp?.min,
            condition = weather?.get(0)?.description,
            icon = weather?.get(0)?.icon,
            pop = pop
        )

    private fun Hourly.toRawHourly(): RawHourly =
        RawHourly(
            time = dt,
            temperature = temp,
            icon = weather?.get(0)?.icon,
            pop = pop
        )
}
