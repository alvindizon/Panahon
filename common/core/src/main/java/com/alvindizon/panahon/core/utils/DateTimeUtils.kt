package com.alvindizon.panahon.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtils{

    private val DEFAULT_FORMAT_LOCALE = Locale.ENGLISH
    const val HOURLY_PATTERN = "ha" // example: 5 PM
    const val EXACT_HOURLY_PATTERN = "h:mm a" // example: 5:55 PM
    const val DAILY_PATTERN = "EEE d MMM" // example: Thu Jun 9
    const val COMPLETE_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ssZ"
    const val HOURLY_ITEMS = 24

    fun Long.convertTimestampToString(format: String, timezone: String?): String {
        val instant = Instant.ofEpochSecond(this)
        val df = DateTimeFormatter.ofPattern(format, DEFAULT_FORMAT_LOCALE)
        val zoneId = timezone?.let { ZoneId.of(it) } ?: ZoneId.systemDefault()
        return instant.atZone(zoneId).format(df).toString()
    }

    fun getCurrentTimeString(format: String, timezone: String?): String {
        val instant = Instant.now()
        val df = DateTimeFormatter.ofPattern(format, DEFAULT_FORMAT_LOCALE)
        val zoneId = timezone?.let { ZoneId.of(it) } ?: ZoneId.systemDefault()
        return instant.atZone(zoneId).format(df).toString()
    }
}
