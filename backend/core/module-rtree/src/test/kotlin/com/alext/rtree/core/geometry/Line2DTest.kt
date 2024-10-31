package com.alext.rtree.core.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Line2DTest {

  @Test
  fun `intersect - case 1`() {
    val a = Line2D(0.0, 0.0, 1.0, 1.0)
    val b = Line2D(1.0, 1.0, 2.0, 2.0)
    assertThat(a.intersectsLine(b)).isEqualTo(true)
  }

  @Test
  fun `intersect - case 2`() {
    val a = Line2D(0.0, 0.0, 1.0, 1.0)
    val b = Line2D(2.0, 2.0, 3.0, 3.0)
    assertThat(a.intersectsLine(b)).isEqualTo(false)
  }

  @Test
  fun `intersect - case 3`() {
    val a = Line2D(0.0, 0.0, 2.0, 2.0)
    val b = Line2D(0.0, 2.0, 2.0, 0.0)
    assertThat(a.intersectsLine(b)).isEqualTo(true)
  }
}
