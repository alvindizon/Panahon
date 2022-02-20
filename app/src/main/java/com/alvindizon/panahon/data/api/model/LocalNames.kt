package com.alvindizon.panahon.data.api.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocalNames(
    @Json(name = "en")
    val en: String
)
