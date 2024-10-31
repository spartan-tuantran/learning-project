package com.alext.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class JavaTimeModule : SimpleModule() {
  init {
    addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer())
    addDeserializer(OffsetDateTime::class.java, OffsetDateTimeDeserializer())
    addSerializer(Instant::class.java, InstantSerializer())
    addDeserializer(Instant::class.java, InstantDeserializer())
    addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
    addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
    addSerializer(ChronoUnit::class.java, ChronoUnitSerializer())
    addDeserializer(ChronoUnit::class.java, ChronoUnitDeserializer())
    addSerializer(LocalDate::class.java, LocalDateSerializer())
    addDeserializer(LocalDate::class.java, LocalDateDeserializer())
  }
}
