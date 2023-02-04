package com.alvindizon.panahon.locations.model

data class RawSimpleForecast(
    val locationName: String,
    val currentTemp: Double,
    val condition: String,
    val icon: String
)