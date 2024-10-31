package com.alext.database.exposed.function

import java.time.Instant

fun Instant.plusDays(days: Long): Instant = plusSeconds(days * 24 * 60 * 60)
fun Instant.minusDays(days: Long): Instant = minusSeconds(days * 24 * 60 * 60)
