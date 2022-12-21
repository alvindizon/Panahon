package com.alvindizon.panahon.common.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


interface PreferencesManager {
    fun getTemperatureUnit(): Flow<Temperature>
    suspend fun setTemperatureUnit(unit: Temperature)
    fun getSpeedUnit(): Flow<Speed>
    suspend fun setSpeedUnit(unit: Speed)
    fun getPressureUnit(): Flow<Pressure>
    suspend fun setPressureUnit(unit: Pressure)
    fun getDistanceUnit(): Flow<Distance>
    suspend fun setDistanceUnit(unit: Distance)
}

class PreferencesManagerImpl(private val dataStore: DataStore<Preferences>) : PreferencesManager {

    override fun getTemperatureUnit(): Flow<Temperature> = dataStore.data.map { preference ->
        preference[PREF_TEMP_UNIT_KEY]?.let {
            enumValueOf<Temperature>(it)
        } ?: Temperature.Celsius
    }

    override suspend fun setTemperatureUnit(unit: Temperature) {
       dataStore.edit { it[PREF_TEMP_UNIT_KEY] = unit.name }
    }

    override fun getSpeedUnit(): Flow<Speed> = dataStore.data.map { preference ->
        preference[PREF_SPEED_UNIT_KEY]?.let {
            enumValueOf<Speed>(it)
        } ?: Speed.MetersPerSec
    }

    override suspend fun setSpeedUnit(unit: Speed) {
        dataStore.edit { it[PREF_SPEED_UNIT_KEY] = unit.name }
    }

    override fun getPressureUnit(): Flow<Pressure> = dataStore.data.map { preference ->
        preference[PREF_PRESSURE_UNIT_KEY]?.let {
            enumValueOf<Pressure>(it)
        } ?: Pressure.HectoPascals
    }

    override suspend fun setPressureUnit(unit: Pressure) {
        dataStore.edit { it[PREF_PRESSURE_UNIT_KEY] = unit.name }
    }

    override fun getDistanceUnit(): Flow<Distance> = dataStore.data.map { preference ->
        preference[PREF_DISTANCE_UNIT_KEY]?.let {
            enumValueOf<Distance>(it)
        } ?: Distance.Kilometers
    }

    override suspend fun setDistanceUnit(unit: Distance) {
        dataStore.edit { it[PREF_DISTANCE_UNIT_KEY] = unit.name }
    }


    companion object {
        const val PREFERENCES_NAME = "panahonSettings"
        val PREF_TEMP_UNIT_KEY = stringPreferencesKey("tempUnitKey")
        val PREF_SPEED_UNIT_KEY = stringPreferencesKey("speedUnitKey")
        val PREF_PRESSURE_UNIT_KEY = stringPreferencesKey("pressureUnitKey")
        val PREF_DISTANCE_UNIT_KEY = stringPreferencesKey("distanceUnitKey")
    }
}