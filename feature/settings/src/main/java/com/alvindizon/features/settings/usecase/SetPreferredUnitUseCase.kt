package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SetPreferredUnitUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {

    suspend operator fun invoke(sign: String) {
        when (sign) {
            Temperature.Celsius.sign,
            Temperature.Fahrenheit.sign -> {
                Temperature.values().find { it.sign == sign }
                    ?.let { unit -> preferencesManager.setTemperatureUnit(unit) }
            }
            Speed.MetersPerSec.sign,
            Speed.KilometersPerHour.sign,
            Speed.MilesPerHour.sign -> {
                Speed.values().find { it.sign == sign }
                    ?.let { unit -> preferencesManager.setSpeedUnit(unit) }
            }
            Pressure.HectoPascals.sign,
            Pressure.InchOfMercury.sign -> {
                Pressure.values().find { it.sign == sign }
                    ?.let { unit -> preferencesManager.setPressureUnit(unit) }
            }
            Distance.Kilometers.sign,
            Distance.Miles.sign -> {
                Distance.values().find { it.sign == sign }
                    ?.let { unit -> preferencesManager.setDistanceUnit(unit) }
            }
        }
    }
}