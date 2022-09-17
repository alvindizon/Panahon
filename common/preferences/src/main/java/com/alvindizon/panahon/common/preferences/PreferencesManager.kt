package com.alvindizon.panahon.common.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alvindizon.panahon.core.units.Temperature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


interface PreferencesManager {
    fun getTemperatureUnit(): Flow<Temperature>
    suspend fun setTemperatureUnit(unit: Temperature)
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

    companion object {
        const val PREFERENCES_NAME = "panahonSettings"
        val PREF_TEMP_UNIT_KEY = stringPreferencesKey("tempUnitKey")
    }
}