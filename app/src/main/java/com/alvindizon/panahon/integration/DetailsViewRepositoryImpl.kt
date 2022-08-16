package com.alvindizon.panahon.integration

import com.alvindizon.panahon.api.model.Daily
import com.alvindizon.panahon.api.model.Hourly
import com.alvindizon.panahon.core.utils.convertTimestampToString
import com.alvindizon.panahon.details.integration.DetailsViewRepository
import com.alvindizon.panahon.details.model.DailyForecast
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.HourlyForecast
import com.alvindizon.panahon.repo.PanahonRepo
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class DetailsViewRepositoryImpl @Inject constructor(
    private val panahonRepo: PanahonRepo
) : DetailsViewRepository {

    override suspend fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): DetailedForecast {
        return panahonRepo.getWeatherForLocation(latitude, longitude).run {
            DetailedForecast(
                locationName,
                current.sunrise?.toLong()?.convertTimestampToString(EXACT_HOURLY_PATTERN, timezone)
                    ?.lowercase(),
                current.sunset?.toLong()?.convertTimestampToString(EXACT_HOURLY_PATTERN, timezone)
                    ?.lowercase(),
                current.temp.roundToInt().toString(),
                current.feelsLike?.roundToInt().toString(),
                current.weather[0].description,
                current.weather[0].icon,
                hourly?.take(HOURLY_ITEMS)?.map { mapResponseToHourlyForecast(it, timezone) },
                daily?.drop(1)?.map { mapResponseToDailyForecast(it, timezone) }
            )
        }
    }

    private fun mapResponseToDailyForecast(dailyResponse: Daily, timezone: String?): DailyForecast =
        with(dailyResponse) {
            DailyForecast(
                dt?.toLong()?.convertTimestampToString(DAILY_PATTERN, timezone),
                temp?.max?.roundToInt().toString(),
                temp?.min?.roundToInt().toString(),
                weather?.get(0)?.description,
                weather?.get(0)?.icon,
            )
        }

    private fun mapResponseToHourlyForecast(
        hourlyResponse: Hourly,
        timezone: String?
    ): HourlyForecast =
        with(hourlyResponse) {
            HourlyForecast(
                dt?.toLong()?.convertTimestampToString(HOURLY_PATTERN, timezone)?.lowercase(),
                temp?.roundToInt().toString(),
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
