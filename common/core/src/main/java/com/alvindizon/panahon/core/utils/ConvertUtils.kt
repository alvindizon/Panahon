package com.alvindizon.panahon.core.utils

import com.alvindizon.panahon.core.units.Temperature
import kotlin.math.roundToInt

fun Double.celsiusToOthers(unit: Temperature): String {
    return when(unit) {
        Temperature.Celsius -> "${this.roundToInt()}${unit.sign}"
        Temperature.Fahrenheit -> "${(this * 1.8 + 32).roundToInt()}${unit.sign}"
    }
}