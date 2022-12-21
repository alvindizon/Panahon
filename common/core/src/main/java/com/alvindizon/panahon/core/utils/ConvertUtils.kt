package com.alvindizon.panahon.core.utils

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.core.units.toTemperatureString
import kotlin.math.roundToInt

fun Double.celsiusToOthers(unit: Temperature): String {
    return when(unit) {
        Temperature.Celsius -> toTemperatureString(unit)
        Temperature.Fahrenheit -> (this * 1.8 + 32).toTemperatureString(unit)
    }
}