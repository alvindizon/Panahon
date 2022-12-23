package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Speed
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@ViewModelScoped
class FetchPreferredSpeedIndexUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {

    suspend operator fun invoke(): Int {
        val unit = preferencesManager.getSpeedUnit().first()
        return Speed.valueOf(unit.name).ordinal
    }
}