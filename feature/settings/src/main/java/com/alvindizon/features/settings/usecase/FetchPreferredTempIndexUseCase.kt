package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@ViewModelScoped
class FetchPreferredTempIndexUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {

    suspend operator fun invoke(): Int {
        val unit = preferencesManager.getTemperatureUnit().first()
        return Temperature.valueOf(unit.name).ordinal
    }
}