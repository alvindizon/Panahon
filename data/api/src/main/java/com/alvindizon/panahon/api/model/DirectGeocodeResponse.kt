package com.alvindizon.panahon.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DirectGeocodeResponseItem(
    @Json(name = "country")
    val country: String,
    @Json(name = "lat")
    val lat: Double,
    @Json(name = "lon")
    val lon: Double,
    @Json(name = "name")
    val name: String,
    @Json(name = "state")
    val state: String?
)
