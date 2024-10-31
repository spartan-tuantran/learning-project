package com.alext.rtree.core.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LineTest {

  private val factory: Geometries = Geometries

  @Test
  fun `lines intersect`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.line(0.0, -1.0, 0.0, 1.0)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `lines does not intersect`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.line(1.1, -1.0, 1.1, 1.0)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line intersect rectangle`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.rectangle(0.2, -0.5, 0.8, 0.5)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `line does not intersect rectangle`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.rectangle(1.2, -0.5, 1.8, 0.5)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line intersect circle`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(0.0, 0.5, 1.0)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `line does not intersect circle`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(0.0, 0.5, 0.4)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line does not intersect circle EAST`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(1.5, 0.0, 0.4)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line intersect circle EAST`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(1.5, 0.0, 0.6)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `line does not intersect circle WEST`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(-1.5, 0.0, 0.4)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line intersect circle WEST`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(-1.5, 0.0, 0.6)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `line does intersect circle NORTH`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(0.0, 1.5, 0.4)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line does not intersect circle SOUTH`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(0.0, 1.5, 0.4)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line intersect circle SOUTH`() {
    val a = factory.line(-1.0, 0.0, 1.0, 0.0)
    val b = factory.circle(0.0, 1.5, 0.6)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `constructor check`() {
    val a = factory.line(-2.0, 3.0, 1.0, -1.0)
    val mbr = a.mbr
    assertThat(mbr).isEqualTo(factory.rectangle(-2.0, -1.0, 1.0, 3.0))
    assertThat(a.x1).isEqualTo(-2.0)
    assertThat(a.y1).isEqualTo(3.0)
    assertThat(a.x2).isEqualTo(1.0)
    assertThat(a.y2).isEqualTo(-1.0)
  }

  @Test
  fun `line same x within circle`() {
    val a = factory.line(1.0, 2.0, 1.0, 4.0)
    val b = factory.circle(1.0, 3.0, 2.0)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `line is the point within circle`() {
    val a = factory.line(1.0, 2.0, 1.0, 2.0)
    val b = factory.circle(1.0, 3.0, 2.0)
    assertThat(a.intersects(b)).isEqualTo(true)
  }

  @Test
  fun `line point outside of circle`() {
    val a = factory.line(1.0, 10.0, 1.0, 10.0)
    val b = factory.circle(1.0, 3.0, 2.0)
    assertThat(a.intersects(b)).isEqualTo(false)
  }

  @Test
  fun `line distance to rectangle`() {
    val a = factory.line(1.0, 2.0, 1.0, 2.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(Math.sqrt(5.0))
  }

  @Test
  fun `0 distance when one end is inside`() {
    val a = factory.line(1.0, 2.0, 4.0, 4.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(0.0)
  }

  @Test
  fun `0 distance when other end is inside`() {
    val a = factory.line(4.0, 4.0, 1.0, 2.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(0.0)
  }

  @Test
  fun `0 distance WEST edge`() {
    val a = factory.line(3.0, 1.0, 3.0, 10.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(0.0)
  }

  @Test
  fun `0 distance contains NORTH edge`() {
    val a = factory.line(2.0, 7.0, 10.0, 7.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(0.0)
  }

  @Test
  fun `0 distance contains SOUTH edge`() {
    val a = factory.line(2.0, 3.0, 10.0, 3.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(0.0)
  }

  @Test
  fun `0 distance contains EAST edge`() {
    val a = factory.line(7.0, 1.0, 7.0, 10.0)
    val r = factory.rectangle(3.0, 3.0, 7.0, 7.0)
    assertThat(a.distance(r)).isEqualTo(0.0)
  }

  @Test
  fun `line does not intersect point`() {
    assertThat(
      factory.line(1.5, 1.5, 2.6, 2.5).intersects(factory.point(2.0, 2.0))
    ).isEqualTo(false)
  }

  @Test
  fun `line intersect point`() {
    assertThat(
      factory.line(1.5, 1.5, 2.5, 2.5).intersects(factory.point(2.0, 2.0))
    ).isEqualTo(true)
  }
}
