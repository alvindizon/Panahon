package com.alvindizon.panahon.details.data

import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.model.RawDetailedForecast
import kotlinx.coroutines.flow.Flow

interface DetailsViewRepository {

    fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String,
    ): Flow<RawDetailedForecast>

    fun fetchTemperatureUnit(): Flow<Temperature>

    fun fetchSpeedUnit(): Flow<Speed>

    fun fetchPressureUnit(): Flow<Pressure>

    fun fetchDistanceUnit(): Flow<Distance>
}
