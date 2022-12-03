package com.alvindizon.panahon.details.data

import com.alvindizon.panahon.details.model.DetailedForecast

interface DetailsViewRepository {

    suspend fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): DetailedForecast
}
