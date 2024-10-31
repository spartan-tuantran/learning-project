package com.alext.javatime

import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun OffsetDateTime.asEpochMillis(): Long {
  return toInstant().toEpochMilli()
}

fun Long.asOffsetDateTime(): OffsetDateTime {
  return Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC)
}

fun Timestamp.asOffsetDateTime(): OffsetDateTime {
  val instant = Instant.ofEpochMilli(this.time)
  return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
}

fun Instant.asOffsetDateTime(): OffsetDateTime {
  return atOffset(ZoneOffset.UTC)
}

fun LocalDateTime.asOffsetDateTime(): OffsetDateTime {
  return this.atOffset(ZoneOffset.UTC)
}

fun LocalDate.asOffsetDateTime(): OffsetDateTime {
  return this.atTime(LocalTime.MIDNIGHT).atOffset(ZoneOffset.UTC)
}
