package com.alvindizon.panahon.core.utils

import com.alvindizon.panahon.core.units.*
import java.math.RoundingMode

fun Double.celsiusToOthers(unit: Temperature): String {
    return when (unit) {
        Temperature.Celsius -> toTemperatureString(unit)
        Temperature.Fahrenheit -> (this * 1.8 + 32).toTemperatureString(unit)
    }
}

fun Double.msToOthers(unit: Speed): String {
    return when (unit) {
        Speed.MetersPerSec -> "${toBigDecimal().setScale(2, RoundingMode.HALF_UP)} ${unit.sign}"
        Speed.KilometersPerHour -> "${
            (toBigDecimal() * 3.60.toBigDecimal()).setScale(
                2,
                RoundingMode.HALF_UP
            )
        } ${unit.sign}"
        Speed.MilesPerHour -> "${
            (2.236936.toBigDecimal().times(toBigDecimal())).setScale(2, RoundingMode.HALF_UP)
        } ${unit.sign}"
    }
}

fun Int.hPaToOthers(unit: Pressure): String {
    return when (unit) {
        Pressure.HectoPascals -> "$this ${unit.sign}"
        Pressure.InchOfMercury -> "${
            (0.02952998751.toBigDecimal().times(toBigDecimal())).setScale(2, RoundingMode.HALF_UP)
        } ${unit.sign}"
    }
}

fun Int.metersToOthers(unit: Distance): String {
    return when (unit) {
        Distance.Kilometers -> "${
            (this.toBigDecimal().times((0.001).toBigDecimal())).setScale(2, RoundingMode.HALF_UP)
        } ${unit.sign}"
        Distance.Miles -> "${
            (this.toBigDecimal().times((0.000621371).toBigDecimal())).setScale(2, RoundingMode.HALF_UP)
        } ${unit.sign}"
    }
}