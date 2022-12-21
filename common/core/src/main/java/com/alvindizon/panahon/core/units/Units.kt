package com.alvindizon.panahon.core.units

import kotlin.math.roundToInt

enum class Temperature(val sign: String) {
    Celsius("°C"),
    Fahrenheit("°F");
}

fun Double.toTemperatureString(temperature: Temperature): String =
    "${roundToInt()}${temperature.sign}"