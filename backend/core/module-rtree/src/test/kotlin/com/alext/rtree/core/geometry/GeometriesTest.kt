package com.alext.rtree.core.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GeometriesTest {

  private val factory: Geometries = Geometries

  @Test
  fun `mbr for 2`() {
    val a = factory.rectangle(0.0, 0.0, 4.0, 4.0)
    val b = factory.rectangle(0.0, 0.0, 2.0, 2.0)
    assertThat(factory.mbr(listOf(a, b))).isEqualTo(a)
  }

  @Test
  fun `mbr for 3`() {
    val a = factory.rectangle(0.0, 0.0, 4.0, 4.0)
    val b = factory.rectangle(0.0, 0.0, 2.0, 2.0)
    val c = factory.rectangle(1.0, 1.0, 2.0, 2.0)
    assertThat(factory.mbr(listOf(a, b, c))).isEqualTo(a)
  }
}
