package com.alext.javatime

import java.time.OffsetDateTime
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class JavaTimeConverterTest {

  @Test
  fun `convert Epoch Millis to OffsetDateTime vice versa`() {
    val originOffset = OffsetDateTime.parse("2023-06-24T02:27:46.478Z")
    val asEpoch = originOffset.asEpochMillis()
    val backToOrigin = asEpoch.asOffsetDateTime()
    Assertions.assertThat(backToOrigin).isEqualTo(originOffset)
  }
}
