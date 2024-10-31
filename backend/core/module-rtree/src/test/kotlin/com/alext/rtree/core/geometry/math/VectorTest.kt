package com.alext.rtree.core.geometry.math

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class VectorTest {

  @ParameterizedTest(name = "(a{0}, a{1}).b({2}, {3}) = {4}")
  @CsvSource(
    "1.0, 2.0, 1.0, 2.0, 5.0",
    "1.0, 2.0, 1.0, 3.0, 7.0",
    "2.0, 2.0, 2.0, 2.0, 8.0"
  )
  fun dot(x1: Double, y1: Double, x2: Double, y2: Double, result: Double) {
    val a = Vector(x1, y1)
    val b = Vector(x2, y2)
    assertThat(a.dot(b)).isEqualTo(result)
  }

  @Test
  fun times() {
    val result = Vector(1.0, 2.0) * 3.0
    assertThat(result).isEqualTo(Vector(3.0, 6.0))
  }

  @Test
  fun minus() {
    val result = Vector(1.0, 2.0) - Vector(1.0, 2.0)
    assertThat(result).isEqualTo(Vector(0.0, 0.0))
  }

  @ParameterizedTest(name = "(vector({0}^2 + {1}^2) = {2}")
  @CsvSource(
    "1.0, 2.0, 5.0",
    "2.0, 2.0, 8.0",
    "3.0, 2.0, 13.0"
  )
  fun modulusSquared(x: Double, y: Double, result: Double) {
    assertThat(Vector(x, y).modulusSquared()).isEqualTo(result)
  }

  @ParameterizedTest(name = "(vector({0}^2 + {1}^2) = {2}")
  @CsvSource(
    "1.0, 2.0, 5.0",
    "2.0, 2.0, 8.0",
    "3.0, 2.0, 13.0"
  )
  fun modulus(x: Double, y: Double, result: Double) {
    assertThat(Vector(x, y).modulus()).isEqualTo(Math.sqrt(result))
  }
}
