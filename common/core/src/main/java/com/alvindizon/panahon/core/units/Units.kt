package com.alvindizon.panahon.core.units

import kotlin.math.roundToInt

enum class Temperature(val sign: String) {
    Celsius("°C"),
    Fahrenheit("°F");
}

enum class Speed(val sign: String) {
    MetersPerSec("m/s"),
    KilometersPerHour("km/h"),
    MilesPerHour("mi/h");
}

enum class Pressure(val sign: String) {
    HectoPascals("hPa"),
    InchOfMercury("inHg");
}

enum class Distance(val sign: String) {
    Kilometers("km"),
    Miles("mi");
}

fun Double.toTemperatureString(temperature: Temperature): String =
    "${roundToInt()}${temperature.sign}"