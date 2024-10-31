package com.alext.rtree.core.geometry

import java.util.Random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointTest {

  private val factory: Geometries = Geometries
  private val random = Random()

  /**
   * Return a random coordinate between [0, 100.0]
   */
  private fun rnd(): Double = random.nextDouble() * 100.0

  @Test
  fun `constructor check`() {
    val point = factory.point(1.0, 2.0)
    assertThat(point.x).isEqualTo(1.0)
    assertThat(point.y).isEqualTo(2.0)
    assertThat(point.x1).isEqualTo(1.0)
    assertThat(point.y1).isEqualTo(2.0)
    assertThat(point.x2).isEqualTo(1.0)
    assertThat(point.y2).isEqualTo(2.0)
    assertThat(point.mbr).isEqualTo(point)
    assertThat(point.geometry).isEqualTo(point)
  }

  @Test
  fun `distance to rectangle`() {
    val point = factory.point(0.0, 0.0)
    val rectangle = factory.rectangle(1.0, 1.0, 2.0, 2.0)
    assertThat(point.distance(rectangle)).isEqualTo(Math.sqrt(2.0))
  }

  @Test
  fun `intersect rectangle`() {
    val point = factory.point(0.0, 0.0)
    val rectangle = factory.rectangle(0.0, 0.0, 2.0, 2.0)
    assertThat(point.intersects(rectangle)).isEqualTo(true)
  }

  @Test
  fun `does not intersect rectangle`() {
    val point = factory.point(0.0, 0.0)
    val rectangle = factory.rectangle(1.0, 1.0, 2.0, 2.0)
    assertThat(point.intersects(rectangle)).isEqualTo(false)
  }

  @Test
  fun `contains point`() {
    val point = factory.point(0.0, 0.0)
    assertThat(point.contains(0.0, 0.0)).isEqualTo(true)
    assertThat(point.contains(1.0, 0.0)).isEqualTo(false)
  }

  @Test
  fun `area is always 0`() {
    assertThat(factory.point(rnd(), rnd()).area()).isEqualTo(0.0)
  }

  @Test
  fun `intersection area is always 0`() {
    val point = factory.point(rnd(), rnd())
    val rectangle = factory.rectangle(rnd(), rnd(), rnd(), rnd())
    assertThat(point.intersectionArea(rectangle)).isEqualTo(0.0)
  }

  @Test
  fun `perimeter is always 0`() {
    val point = factory.point(rnd(), rnd())
    assertThat(point.perimeter()).isEqualTo(0.0)
  }

  @Test
  fun `merge with a rectangle`() {
    val point = factory.point(1.0, 1.0)
    val rectangle = factory.rectangle(-1.0, -2.0, 1.0, 1.0)
    assertThat(point.merge(rectangle)).isEqualTo(factory.rectangle(-1.0, -2.0, 1.0, 1.0))
  }

  @Test
  fun `hash code and equals`() {
    assertThat(
      setOf(
        factory.point(0.0, 0.0),
        factory.point(0.0, 0.0)
      )
    ).hasSize(1)
    assertThat(factory.point(0.0, 0.0)).isEqualTo(factory.point(0.0, 0.0))
  }
}
