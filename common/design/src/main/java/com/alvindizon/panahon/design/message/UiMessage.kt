package com.alvindizon.panahon.design.message

import java.util.*

data class UiMessage(
    val message: String,
    val id: Long = UUID.randomUUID().mostSignificantBits,
)
