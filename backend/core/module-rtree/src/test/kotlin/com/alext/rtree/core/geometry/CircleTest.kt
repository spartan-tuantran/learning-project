package com.alext.rtree.core.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CircleTest {

  private val factory: Geometries = Geometries

  @Test
  fun `constructor check`() {
    val c = factory.circle(0.0, 1.0, 5.0)
    assertThat(c.x).isEqualTo(0.0)
    assertThat(c.y).isEqualTo(1.0)
    assertThat(c.radius).isEqualTo(5.0)
  }

  @Test
  fun `circle distance`() {
    val c = factory.circle(0.0, 0.0, 1.0)
    val r = factory.rectangle(4.0, 4.0, 8.0, 8.0)
    assertThat(c.distance(r)).isEqualTo(4.656854249492381)
  }

  @Test
  fun `circle mbr`() {
    val c = factory.circle(0.0, 0.0, 5.0)
    assertThat(c.mbr).isEqualTo(factory.rectangle(-5.0, -5.0, 5.0, 5.0))
  }

  @Test
  fun `circle intersects rectangle`() {
    val c = factory.circle(0.0, 0.0, 1.0)
    val r = factory.rectangle(0.0, 0.0, 1.0, 1.0)
    assertThat(c.intersects(r)).isEqualTo(true)
  }

  @Test
  fun `circle does not intersects rectangle`() {
    val c = factory.circle(0.0, 0.0, 1.0)
    val r = factory.rectangle(2.0, 2.0, 4.0, 4.0)
    assertThat(c.intersects(r)).isEqualTo(false)
  }

  @Test
  fun `circle intersects point`() {
    val c = factory.circle(0.0, 0.0, 5.0)
    val p = factory.point(1.0, 1.0)
    assertThat(c.intersects(p)).isEqualTo(true)
  }

  @Test
  fun `circle does not intersects point`() {
    val c = factory.circle(0.0, 0.0, 5.0)
    val p = factory.point(6.0, 6.0)
    assertThat(c.intersects(p)).isEqualTo(false)
  }

  @Test
  fun `circle intersect line`() {
    val c = factory.circle(0.0, 0.0, 5.0)
    val l = factory.line(0.0, 0.0, 5.0, 5.0)
    assertThat(c.intersects(l)).isEqualTo(true)
  }

  @Test
  fun `circle does not intersect line`() {
    val c = factory.circle(0.0, 0.0, 5.0)
    val l = factory.line(4.0, 4.0, 5.0, 5.0)
    assertThat(c.intersects(l)).isEqualTo(false)
  }

  @Test
  fun `hash code and equals`() {
    assertThat(
      setOf(
        factory.circle(0.0, 0.0, 5.0),
        factory.circle(0.0, 0.0, 5.0)
      )
    ).hasSize(1)
    assertThat(factory.circle(0.0, 0.0, 5.0)).isEqualTo(factory.circle(0.0, 0.0, 5.0))
  }
}
