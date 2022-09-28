package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchPreferredTempIndexUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {

    suspend fun execute(): Int {
        val unit = preferencesManager.getTemperatureUnit().first()
        return Temperature.valueOf(unit.name).ordinal
    }
}