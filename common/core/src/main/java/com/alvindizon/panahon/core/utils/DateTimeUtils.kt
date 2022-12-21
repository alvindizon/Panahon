package com.alvindizon.panahon.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val DEFAULT_FORMAT_LOCALE = Locale.ENGLISH

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
