package com.alvindizon.panahon.details.data

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.model.DetailedForecast
import kotlinx.coroutines.flow.Flow

interface DetailsViewRepository {

    suspend fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String,
        temperature: Temperature
    ): DetailedForecast

    fun fetchTemperatureUnit(): Flow<Temperature>
}
