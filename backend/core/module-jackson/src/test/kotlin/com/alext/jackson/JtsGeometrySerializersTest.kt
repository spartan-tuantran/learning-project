package com.alext.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.PrecisionModel

class JtsGeometrySerializersTest {

  companion object {
    private val FACTORY = GeometryFactory(PrecisionModel(), 4326)
    private val MAPPER = ObjectMapper().configured()
  }

  @Test
  fun `Point() round trip`() {
    val point = FACTORY.createPoint(Coordinate(1.0, 2.0))
    val json = MAPPER.writeValueAsString(point).also {
      println(it)
    }
    val result = MAPPER.readValue(json, Point::class.java).also {
      println(it)
    }
    assertThat(result.srid).isEqualTo(4326)
    assertThat(point.x).isEqualTo(result.x)
    assertThat(point.y).isEqualTo(result.y)
  }

  @Test
  fun `LinearRing() round trip`() {
    val ring = FACTORY.createLinearRing(
      MutableList(5) { Coordinate(1.0, 2.0) }.toTypedArray()
    )

    val json = MAPPER.writeValueAsString(ring).also {
      println(it)
    }
    val result = MAPPER.readValue(json, LinearRing::class.java).also {
      println(it)
    }
    (0..4).map { i ->
      assertThat(result.coordinates[i].x).isEqualTo(ring.coordinates[i].x)
      assertThat(result.coordinates[i].y).isEqualTo(ring.coordinates[i].y)
      assertThat(result.srid).isEqualTo(4326)
    }
  }

  @Test
  fun `Polygon() round trip`() {
    val polygon = FACTORY.createPolygon(
      FACTORY.createLinearRing(
        MutableList(5) { Coordinate(1.0, 2.0) }.toTypedArray()
      )
    )
    val json = MAPPER.writeValueAsString(polygon).also {
      println(it)
    }
    val result = MAPPER.readValue(json, Polygon::class.java).also {
      println(it)
    }
    assertThat(result.srid).isEqualTo(4326)
    val ring = result.exteriorRing
    (0..4).map { i ->
      assertThat(result.coordinates[i].x).isEqualTo(ring.coordinates[i].x)
      assertThat(result.coordinates[i].y).isEqualTo(ring.coordinates[i].y)
    }
  }

  @Test
  fun `Multipolygon() round trip`() {
    val polygon = FACTORY.createPolygon(
      FACTORY.createLinearRing(
        MutableList(5) { Coordinate(1.0, 2.0) }.toTypedArray()
      )
    )
    val multiPolygon = FACTORY.createMultiPolygon(arrayOf(polygon, polygon))
    val json = MAPPER.writeValueAsString(multiPolygon).also {
      println(it)
    }
    val result = MAPPER.readValue(json, MultiPolygon::class.java).also {
      println(it)
    }

    assertThat(result.srid).isEqualTo(4326)
    assertThat(result.numGeometries).isEqualTo(2)
    (0 until result.numGeometries).forEach { i ->
      assertThat(result.getGeometryN(i)).isEqualTo(polygon)
    }
  }
}
