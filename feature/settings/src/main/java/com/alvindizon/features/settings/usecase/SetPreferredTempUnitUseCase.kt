package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetPreferredTempUnitUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {

    suspend fun execute(selectedIndex: Int) {
        val temperature = Temperature.values()[selectedIndex]
        preferencesManager.setTemperatureUnit(temperature)
    }
}