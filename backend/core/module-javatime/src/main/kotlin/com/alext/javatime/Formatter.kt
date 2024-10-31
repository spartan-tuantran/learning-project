package com.alext.javatime

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object Formatter {
  private val OFFSET: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
  private val LOCAL: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  private val LOCAL_DATE: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
  private val INSTANT: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

  fun format(offset: OffsetDateTime): String {
    return OFFSET.format(offset)
  }

  fun offset(text: String): OffsetDateTime {
    return OFFSET.parse(text, OffsetDateTime::from)
  }

  fun format(offset: LocalDateTime): String {
    return LOCAL.format(offset)
  }

  fun local(text: String): LocalDateTime {
    return LOCAL.parse(text, LocalDateTime::from)
  }

  fun format(instant: Instant): String {
    return INSTANT.format(instant)
  }

  fun instant(text: String): Instant {
    return INSTANT.parse(text, Instant::from)
  }

  fun format(localDate: LocalDate): String {
    return LOCAL_DATE.format(localDate)
  }

  fun localDate(text: String): LocalDate {
    return LOCAL_DATE.parse(text, LocalDate::from)
  }
}
