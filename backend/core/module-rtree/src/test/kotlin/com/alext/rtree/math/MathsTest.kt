package com.alext.rtree.math

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MathsTest {

  @ParameterizedTest(name = "{0}^2 = {1}")
  @CsvSource(
    "1, 1",
    "2, 4",
    "3, 9"
  )
  fun sqr(x: Double, result: Double) {
    assertThat(Maths.sqr(x)).isEqualTo(result)
  }
}
