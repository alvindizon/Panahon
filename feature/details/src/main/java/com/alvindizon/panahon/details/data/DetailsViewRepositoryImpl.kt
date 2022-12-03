package com.alvindizon.panahon.details.data

import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.Daily
import com.alvindizon.panahon.api.model.Hourly
import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.core.utils.celsiusToOthers
import com.alvindizon.panahon.core.utils.convertTimestampToString
import com.alvindizon.panahon.details.model.DailyForecast
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.HourlyForecast
import kotlinx.coroutines.flow.first
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
        longitude: String
    ): DetailedForecast {
        val tempUnit = preferencesManager.getTemperatureUnit().first()
        return api.getWeather(latitude = latitude, longitude = longitude).run {
            DetailedForecast(
                locationName,
                current.sunrise?.toLong()?.convertTimestampToString(EXACT_HOURLY_PATTERN, timezone)
                    ?.lowercase(),
                current.sunset?.toLong()?.convertTimestampToString(EXACT_HOURLY_PATTERN, timezone)
                    ?.lowercase(),
                current.temp.celsiusToOthers(tempUnit),
                current.feelsLike?.celsiusToOthers(tempUnit),
                current.weather[0].description,
                current.weather[0].icon,
                hourly?.take(HOURLY_ITEMS)?.map { mapResponseToHourlyForecast(it, tempUnit, timezone) },
                daily?.drop(1)?.map { mapResponseToDailyForecast(it, tempUnit, timezone) }
            )
        }
    }

    private fun mapResponseToDailyForecast(dailyResponse: Daily, tempUnit: Temperature, timezone: String?): DailyForecast =
        with(dailyResponse) {
            DailyForecast(
                dt?.toLong()?.convertTimestampToString(DAILY_PATTERN, timezone),
                temp?.max?.celsiusToOthers(tempUnit),
                temp?.min?.celsiusToOthers(tempUnit),
                weather?.get(0)?.description,
                weather?.get(0)?.icon,
            )
        }

    private fun mapResponseToHourlyForecast(
        hourlyResponse: Hourly,
        tempUnit: Temperature,
        timezone: String?
    ): HourlyForecast =
        with(hourlyResponse) {
            HourlyForecast(
                dt?.toLong()?.convertTimestampToString(HOURLY_PATTERN, timezone)?.lowercase(),
                temp?.celsiusToOthers(tempUnit),
                weather?.get(0)?.icon,
            )
        }

    companion object {
        private const val HOURLY_PATTERN = "ha" // example: 5 PM
        private const val EXACT_HOURLY_PATTERN = "h:mm a" // example: 5:55 PM
        private const val DAILY_PATTERN = "EEE d MMM" // example: Thu Jun 9
        private const val HOURLY_ITEMS = 24
    }
}
