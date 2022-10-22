package com.alvindizon.panahon.locations.model

data class LocationForecast(
    val name: String,
    val latitude: String,
    val longitude: String,
    val condition: String,
    val temperature: String,
    val icon: String,
    val isHomeLocation: Boolean
)
