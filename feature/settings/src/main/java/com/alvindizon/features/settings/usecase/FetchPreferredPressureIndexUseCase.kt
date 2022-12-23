package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Pressure
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@ViewModelScoped
class FetchPreferredPressureIndexUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {

    suspend operator fun invoke(): Int {
        val unit = preferencesManager.getPressureUnit().first()
        return Pressure.valueOf(unit.name).ordinal
    }
}