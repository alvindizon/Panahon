package com.alvindizon.panahon.details.model

data class DetailedForecast(
    val locationName: String,
    val sunriseTime: String?,
    val sunsetTime: String?,
    val currentTemp: String?,
    val feelsLikeTemp: String?,
    val condition: String,
    val icon: String,
    val hourly: List<HourlyForecast>?,
    val daily: List<DailyForecast>?,
    val lastUpdatedTime: String,
    val windSpeed: String,
    val pressure: String,
    val visibility: String,
    val uvIndex: String
)

data class DailyForecast(
    val date: String?,
    val maximumTemp: String?,
    val minimumTemp: String?,
    val condition: String?,
    val icon: String?
)

data class HourlyForecast(
    val time: String?,
    val temperature: String?,
    val icon: String?
)
