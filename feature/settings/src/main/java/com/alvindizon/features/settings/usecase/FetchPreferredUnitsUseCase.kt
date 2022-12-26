package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@ViewModelScoped
class FetchPreferredUnitsUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    operator fun invoke(): Flow<List<Int>> = combine(
        preferencesManager.getTemperatureUnit(),
        preferencesManager.getSpeedUnit(),
        preferencesManager.getPressureUnit(),
        preferencesManager.getDistanceUnit()
    ) { temperature, speed, pressure, distance ->
        val tempIndex = Temperature.valueOf(temperature.name).ordinal
        val speedIndex = Speed.valueOf(speed.name).ordinal
        val pressureIndex = Pressure.valueOf(pressure.name).ordinal
        val distanceIndex = Distance.valueOf(distance.name).ordinal
        listOf(tempIndex, speedIndex, pressureIndex, distanceIndex)
    }
}