package com.alvindizon.panahon.details.fakes

import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.data.DetailsViewRepository
import com.alvindizon.panahon.details.model.RawDetailedForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeDetailsViewRepository : DetailsViewRepository {

    private val rawDetailsFlow = MutableSharedFlow<RawDetailedForecast>()
    private val tempUnitFlow = MutableSharedFlow<Temperature>()
    private val speedUnitFlow = MutableSharedFlow<Speed>()
    private val pressureUnitFlow = MutableSharedFlow<Pressure>()
    private val distanceUnitFlow = MutableSharedFlow<Distance>()

    suspend fun emitRawDetails(rawDetailedForecast: RawDetailedForecast) =
        rawDetailsFlow.emit(rawDetailedForecast)

    suspend fun emitTempUnit(temperature: Temperature) = tempUnitFlow.emit(temperature)

    suspend fun emitSpeedUnit(speed: Speed) = speedUnitFlow.emit(speed)

    suspend fun emitPressureUnit(pressure: Pressure) = pressureUnitFlow.emit(pressure)

    suspend fun emitDistanceUnit(distance: Distance) = distanceUnitFlow.emit(distance)

    override fun fetchDetailedForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): Flow<RawDetailedForecast> = rawDetailsFlow

    override fun fetchTemperatureUnit(): Flow<Temperature> = tempUnitFlow

    override fun fetchSpeedUnit(): Flow<Speed> = speedUnitFlow

    override fun fetchPressureUnit(): Flow<Pressure> = pressureUnitFlow

    override fun fetchDistanceUnit(): Flow<Distance> = distanceUnitFlow
}