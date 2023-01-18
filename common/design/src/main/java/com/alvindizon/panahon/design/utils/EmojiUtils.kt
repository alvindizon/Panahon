package com.alvindizon.panahon.design.utils


fun String.toFlagEmoji(): String =
    uppercase()
        .split("")
        .filter { it.isNotBlank() }
        .map { it.codePointAt(0) + 0x1F1A5 }
        .joinToString("") { String(Character.toChars(it)) }