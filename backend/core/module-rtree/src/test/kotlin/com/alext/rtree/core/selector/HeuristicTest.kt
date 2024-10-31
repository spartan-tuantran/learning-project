package com.alext.rtree.core.selector

import com.alext.rtree.core.geometry.Geometries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.RectangleFactory

class HeuristicTest : RectangleFactory {

  @Test
  fun `area of rectangle and geometry`() {
    val r = Geometries.rectangle(0.0, 0.0, 1.0, 1.0)
    val g = Geometries.rectangle(2.0, 2.0, 4.0, 4.0)
    val result = area(r, g)
    assertThat(result).isEqualTo(16.0)
  }

  @Test
  fun `area of rectangle and geometry identical`() {
    val r = Geometries.rectangle(0.0, 0.0, 2.0, 2.0)
    val g = Geometries.rectangle(0.0, 0.0, 2.0, 2.0)
    val result = area(r, g)
    assertThat(result).isEqualTo(4.0)
  }

  @Test
  fun `area overlap should exclude itself`() {
    val r = Geometries.rectangle(0.0, 0.0, 2.0, 2.0)
    val others = listOf(
      Geometries.rectangle(0.0, 0.0, 5.0, 5.0),
      Geometries.rectangle(1.0, 1.0, 2.0, 2.0),
      Geometries.rectangle(2.0, 2.0, 2.0, 2.0)
    )
    val result = areaOverlap(r, others, others[1])
    assertThat(result).isEqualTo(4.0)
  }

  @Test
  fun `area increase`() {
    val result = areaIncrease(
      Geometries.rectangle(0.0, 0.0, 2.0, 2.0),
      Geometries.rectangle(1.0, 1.0, 4.0, 4.0)
    )
    assertThat(result).isGreaterThan(0.0)
  }

  @Test
  fun `area no changes`() {
    val result = areaIncrease(
      Geometries.rectangle(0.0, 0.0, 2.0, 2.0),
      Geometries.rectangle(0.0, 0.0, 4.0, 4.0)
    )
    assertThat(result).isEqualTo(0.0)
  }
}
