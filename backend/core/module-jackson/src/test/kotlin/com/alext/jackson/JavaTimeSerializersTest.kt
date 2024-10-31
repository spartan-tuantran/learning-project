package com.alext.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JavaTimeSerializersTest {

  private val mapper: ObjectMapper = ObjectMapper().configured()

  @Test
  fun `offset date time`() {
    val now = OffsetDateTime.now()
    val json = mapper.writerFor(OffsetDateTime::class.java).writeValueAsString(now)
    val result: OffsetDateTime = mapper.readerFor(OffsetDateTime::class.java).readValue(json)
    assertThat(result).isEqualTo(now)
  }

  @Test
  fun `local date time`() {
    val now = LocalDateTime.now()
    val json = mapper.writerFor(LocalDateTime::class.java).writeValueAsString(now)
    val result: LocalDateTime = mapper.readerFor(LocalDateTime::class.java).readValue(json)
    assertThat(result).isEqualTo(now)
  }

  @Test
  fun `chrono unit`() {
    val weeks = ChronoUnit.WEEKS
    val json = mapper.writerFor(ChronoUnit::class.java).writeValueAsString(weeks)
    val result: ChronoUnit = mapper.readerFor(ChronoUnit::class.java).readValue(json)
    assertThat(result).isEqualTo(weeks)
  }

  @Test
  fun `test all objects`() {
    val all = JavaTime()
    val json = mapper.writeValueAsString(all)
    assertThat(mapper.readerFor(JavaTime::class.java).readValue(json, JavaTime::class.java)).isEqualTo(all)
  }
}

data class JavaTime(
  val offset: OffsetDateTime = OffsetDateTime.now(),
  val local: LocalDateTime = LocalDateTime.now(),
  val chrono: ChronoUnit = ChronoUnit.CENTURIES
)
