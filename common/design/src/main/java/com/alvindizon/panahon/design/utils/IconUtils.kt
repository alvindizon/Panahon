package com.alvindizon.panahon.design.utils

import androidx.annotation.DrawableRes
import com.alvindizon.panahon.design.R


object IconUtils {
    @DrawableRes
    fun getWeatherIconRes(icon: String): Int =
        when(icon) {
            "01d" -> R.drawable.ic_01d
            "01n" -> R.drawable.ic_01n
            "02d" -> R.drawable.ic_02d
            "02n" -> R.drawable.ic_02n
            "03d", "03n" -> R.drawable.ic_03d
            "04d", "04n" -> R.drawable.ic_04d
            "09d", "09n" -> R.drawable.ic_09d
            "10d" -> R.drawable.ic_10d
            "10n" -> R.drawable.ic_10n
            "11d", "11n" -> R.drawable.ic_11d
            "13d", "13n" -> R.drawable.ic_13d
            "50d", "50n" -> R.drawable.ic_50d
            else -> R.drawable.ic_01d
        }
}

