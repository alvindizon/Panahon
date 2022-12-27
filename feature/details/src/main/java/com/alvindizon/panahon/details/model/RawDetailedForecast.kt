package com.alvindizon.panahon.details.model

data class RawDetailedForecast(
    val locationName: String,
    val timezone: String?,
    val sunriseTime: Int?,
    val sunsetTime: Int?,
    val currentTemp: Double,
    val feelsLikeTemp: Double?,
    val condition: String,
    val icon: String,
    val hourly: List<RawHourly>?,
    val daily: List<RawDaily>?,
    val lastUpdatedTime: Int?,
    val windSpeed: Double?,
    val pressure: Int?,
    val visibility: Int?,
    val uvIndex: Double?
)

data class RawDaily(
    val date: Int?,
    val maximumTemp: Double?,
    val minimumTemp: Double?,
    val condition: String?,
    val icon: String?,
    val pop: Double?
)

data class RawHourly(
    val time: Int?,
    val temperature: Double?,
    val icon: String?,
    val pop: Double?
)
