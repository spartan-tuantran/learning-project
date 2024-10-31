package com.alext.javatime

import java.time.LocalDateTime
import java.time.OffsetDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormatterTest {

  @Test
  fun `local round trip`() {
    val now = LocalDateTime.now()
    assertThat(Formatter.local(Formatter.format(now))).isEqualTo(now)
  }

  @Test
  fun `offset round trip`() {
    val now = OffsetDateTime.now()
    assertThat(Formatter.offset(Formatter.format(now))).isEqualTo(now)
  }
}
