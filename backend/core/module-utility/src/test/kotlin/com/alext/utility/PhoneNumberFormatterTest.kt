package com.alext.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PhoneNumberFormatterTest {

  private val formatter = PhoneNumberFormatter.E164

  @Test
  fun `format US phone number`() {
    assertThat(formatter.format("(818)-626-4197")).isEqualTo("+18186264197")
    assertThat(formatter.format("+18186264197")).isEqualTo("+18186264197")
    assertThat(formatter.format("8186264197")).isEqualTo("+18186264197")
  }

  @Test
  fun `format other country phone numbers`() {
    assertThat(formatter.format("01 12 34 56 78", "FR")).isEqualTo("+33112345678")
    assertThat(formatter.format("+84 984681339")).isEqualTo("+84984681339")
  }

  @Test
  fun `format invalid numbers`() {
    assertThat(formatter.format("1")).isEqualTo(null)
    assertThat(formatter.format("+84")).isEqualTo(null)
    assertThat(formatter.format("")).isEqualTo(null)
  }

  @Test
  fun `check various cases`() {
    assertThat(formatter.check("")).isEqualTo(false)
    assertThat(formatter.check("abc")).isEqualTo(false)
    assertThat(formatter.check("1")).isEqualTo(false)
    assertThat(formatter.check("8186264197")).isEqualTo(true)
    assertThat(formatter.check("+18186264197")).isEqualTo(true)
  }
}
