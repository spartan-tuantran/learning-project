package com.alext.rtree.core.geometry

import kotlin.math.sqrt
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RectangleTest {

  private val factory: Geometries = Geometries

  @Test
  fun `rectangle width and height`() {
    val r = factory.rectangle(1.0, 2.0, 3.0, 100.0)
    assertThat(r.width()).isEqualTo(2.0)
    assertThat(r.height()).isEqualTo(98.0)
  }

  @Test
  fun `constructor check`() {
    val r = factory.rectangle(1.0, 2.0, 3.0, 4.0)
    assertThat(r.x1).isEqualTo(1.0)
    assertThat(r.y1).isEqualTo(2.0)
    assertThat(r.x2).isEqualTo(3.0)
    assertThat(r.y2).isEqualTo(4.0)
    assertThat(r.mbr).isEqualTo(r)
    assertThat(r.geometry).isEqualTo(r)
  }

  @Test
  fun `merge another rectangle`() {
    val a = factory.rectangle(0.0, 0.0, 3.0, 4.0)
    val b = factory.rectangle(1.0, 2.0, 3.0, 4.0)
    assertThat(a.merge(b)).isEqualTo(factory.rectangle(0.0, 0.0, 3.0, 4.0))
  }

  @Test
  fun `contains a point`() {
    val r = factory.rectangle(0.0, 0.0, 3.0, 4.0)
    assertThat(r.contains(1.0, 1.0)).isEqualTo(true)
    assertThat(r.contains(0.0, 0.0)).isEqualTo(true)
    assertThat(r.contains(3.0, 4.0)).isEqualTo(true)
    assertThat(r.contains(2.0, 2.0)).isEqualTo(true)
    assertThat(r.contains(-1.0, -1.0)).isEqualTo(false)
  }

  @Test
  fun `intersect with rectangle fully`() {
    val a = factory.rectangle(0.0, 0.0, 3.0, 4.0)
    val b = factory.rectangle(0.0, 0.0, 3.0, 4.0)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `intersect with rectangle partial`() {
    val a = factory.rectangle(0.0, 0.0, 3.0, 4.0)
    val b = factory.rectangle(1.0, 1.0, 3.0, 4.0)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `does not intersect with`() {
    val a = factory.rectangle(0.0, 0.0, 1.0, 1.0)
    val b = factory.rectangle(2.0, 2.0, 4.0, 4.0)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `distance to another rectangle`() {
    val a = factory.rectangle(0.0, 0.0, 1.0, 1.0)
    val b = factory.rectangle(2.0, 2.0, 4.0, 4.0)
    val c = factory.rectangle(100.0, 100.0, 200.0, 200.0)
    assertThat(a.distance(b)).isEqualTo(sqrt(2.0))
    assertThat(a.distance(c)).isEqualTo(140.0071426749364)
  }

  @Test
  fun `intersection area, does not intersect is 0`() {
    val a = factory.rectangle(0.0, 0.0, 1.0, 1.0)
    val b = factory.rectangle(2.0, 2.0, 4.0, 4.0)
    assertThat(a.intersectionArea(b)).isEqualTo(0.0)
  }

  @Test
  fun `intersection area, intersect`() {
    val a = factory.rectangle(0.0, 0.0, 3.0, 4.0)
    val b = factory.rectangle(1.0, 1.0, 3.0, 4.0)
    assertThat(a.intersectionArea(b)).isEqualTo(6.0)
  }

  @Test
  fun `rectangle perimeter`() {
    val r = factory.rectangle(1.0, 1.0, 3.0, 4.0)
    assertThat(r.perimeter()).isEqualTo(10.0)
  }

  @Test
  fun `rectangle area`() {
    val r = factory.rectangle(1.0, 1.0, 3.0, 4.0)
    assertThat(r.area()).isEqualTo(6.0)
  }

  @Test
  fun `hash code and equals`() {
    assertThat(
      setOf(
        factory.rectangle(0.0, 0.0, 1.0, 1.0),
        factory.rectangle(0.0, 0.0, 1.0, 1.0)
      )
    ).hasSize(1)
    assertThat(factory.rectangle(0.0, 0.0, 1.0, 1.0)).isEqualTo(factory.rectangle(0.0, 0.0, 1.0, 1.0))
  }
}
