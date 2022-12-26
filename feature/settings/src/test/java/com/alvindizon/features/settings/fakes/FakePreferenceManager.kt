package com.alvindizon.features.settings.fakes

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakePreferenceManager: PreferencesManager {

    private val tempUnitFlow = MutableSharedFlow<Temperature>()
    private val speedUnitFlow = MutableSharedFlow<Speed>()
    private val pressureUnitFlow = MutableSharedFlow<Pressure>()
    private val distanceUnitFlow = MutableSharedFlow<Distance>()

    suspend fun emitTempUnit(temperature: Temperature) = tempUnitFlow.emit(temperature)

    suspend fun emitSpeedUnit(speed: Speed) = speedUnitFlow.emit(speed)

    suspend fun emitPressureUnit(pressure: Pressure) = pressureUnitFlow.emit(pressure)

    suspend fun emitDistanceUnit(distance: Distance) = distanceUnitFlow.emit(distance)

    override fun getTemperatureUnit(): Flow<Temperature> = tempUnitFlow

    override suspend fun setTemperatureUnit(unit: Temperature) = Unit

    override fun getSpeedUnit(): Flow<Speed> = speedUnitFlow

    override suspend fun setSpeedUnit(unit: Speed) = Unit

    override fun getPressureUnit(): Flow<Pressure> = pressureUnitFlow

    override suspend fun setPressureUnit(unit: Pressure) = Unit

    override fun getDistanceUnit(): Flow<Distance> = distanceUnitFlow

    override suspend fun setDistanceUnit(unit: Distance) = Unit
}