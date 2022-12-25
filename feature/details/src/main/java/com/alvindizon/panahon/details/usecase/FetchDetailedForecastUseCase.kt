package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.core.units.toTemperatureString
import com.alvindizon.panahon.core.utils.*
import com.alvindizon.panahon.core.utils.DateTimeUtils.convertTimestampToString
import com.alvindizon.panahon.details.data.DetailsViewRepository
import com.alvindizon.panahon.details.model.*
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@ViewModelScoped
class FetchDetailedForecastUseCase @Inject constructor(private val repo: DetailsViewRepository) {

    operator fun invoke(
        locationName: String,
        latitude: String,
        longitude: String
    ): Flow<DetailedForecast> =
        combine(
            repo.fetchTemperatureUnit(),
            repo.fetchSpeedUnit(),
            repo.fetchPressureUnit(),
            repo.fetchDistanceUnit(),
            repo.fetchDetailedForecast(locationName, latitude, longitude)
        ) { tempUnit, speedUnit, pressureUnit, distUnit, rawData ->
            with(rawData) {
                DetailedForecast(
                    locationName = locationName,
                    sunriseTime = sunriseTime?.toLong()
                        ?.convertTimestampToString(DateTimeUtils.EXACT_HOURLY_PATTERN, timezone)
                        ?.lowercase(),
                    sunsetTime = sunsetTime?.toLong()
                        ?.convertTimestampToString(DateTimeUtils.EXACT_HOURLY_PATTERN, timezone)
                        ?.lowercase(),
                    currentTemp = currentTemp.celsiusToOthers(tempUnit),
                    feelsLikeTemp = feelsLikeTemp?.celsiusToOthers(tempUnit),
                    condition = condition,
                    icon = icon,
                    hourly = hourly?.take(DateTimeUtils.HOURLY_ITEMS)
                        ?.map { it.toHourlyForecast(tempUnit, timezone) },
                    daily = daily?.drop(1)?.map { it.toDailyForecast(tempUnit, timezone) },
                    lastUpdatedTime = lastUpdatedTime?.toLong()
                        ?.convertTimestampToString(DateTimeUtils.COMPLETE_DATE_TIME, null)
                        ?: DateTimeUtils.getCurrentTimeString(
                            DateTimeUtils.COMPLETE_DATE_TIME,
                            null
                        ),
                    windSpeed = windSpeed?.msToOthers(speedUnit) ?: "0",
                    pressure = pressure?.hPaToOthers(pressureUnit) ?: "0",
                    visibility = visibility?.metersToOthers(distUnit) ?: "0",
                    uvIndex = uvIndex?.toString() ?: "0.0"
                )
            }
        }

    private fun RawDaily.toDailyForecast(tempUnit: Temperature, timezone: String?): DailyForecast =
        DailyForecast(
            date = date?.toLong()?.convertTimestampToString(DateTimeUtils.DAILY_PATTERN, timezone),
            maximumTemp = maximumTemp?.celsiusToOthers(tempUnit),
            minimumTemp = minimumTemp?.celsiusToOthers(tempUnit),
            condition = condition,
            icon = icon,
        )

    private fun RawHourly.toHourlyForecast(
        tempUnit: Temperature,
        timezone: String?
    ): HourlyForecast =
        HourlyForecast(
            time = time?.toLong()?.convertTimestampToString(DateTimeUtils.HOURLY_PATTERN, timezone)
                ?.lowercase(),
            temperature = temperature?.toTemperatureString(tempUnit),
            icon = icon,
        )
}
