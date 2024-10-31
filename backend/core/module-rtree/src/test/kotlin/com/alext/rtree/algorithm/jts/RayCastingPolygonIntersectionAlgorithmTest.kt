package com.alext.rtree.algorithm.jts

import com.alext.rtree.misc.jts.PostgisPoint
import com.alext.rtree.misc.jts.PostgisPolygon
import com.alext.rtree.misc.jts.toJtsGeometry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import test.AbstractRTreeTest
import test.Place

class RayCastingPolygonIntersectionAlgorithmTest : AbstractRTreeTest(), RayCastingPolygonIntersectionAlgorithm {

  @Test
  fun `point in polygon - use center`() {
    REGIONS.forEach { (_, zone) ->
      val polygon = zone.region.toJtsGeometry<Polygon>()
      val expected = polygon.intersects(polygon.centroid)
      val actual = polygon.intersectsWith(polygon.centroid)
      assertThat(expected).isEqualTo(actual)
    }
  }

  @Test
  fun `point in polygon - use random points`() {
    REGIONS.forEach { (_, zone) ->
      val polygon = zone.region.toJtsGeometry<Polygon>()
      Place.values().forEach { place ->
        val point = PostgisPoint(place.lng, place.lat).toJtsGeometry<Point>()
        val expected = polygon.intersects(point)
        val actual = polygon.intersectsWith(point)
        assertThat(expected).isEqualTo(actual)
      }
    }
  }

  @Test
  fun `polygon with just outer ring`() {
    val polygon = PostgisPolygon("POLYGON((50 35, 10 30, 10 10, 30 5, 45 20, 50 35))").toJtsGeometry<Polygon>()
    val inside = PostgisPoint("POINT(30 20)").toJtsGeometry<Point>()
    val outside = PostgisPoint("POINT(0 0)").toJtsGeometry<Point>()
    assertThat(polygon.intersects(inside)).isEqualTo(true)
    assertThat(polygon.intersectsWith(inside)).isEqualTo(true)
    assertThat(polygon.intersects(outside)).isEqualTo(false)
    assertThat(polygon.intersectsWith(outside)).isEqualTo(false)
  }

  /**
   * (0,0)     (10,0)
   *  ***********
   *  *  *****  *
   *  *  *   *  *
   *  *  *****  *
   *  ***********
   * (0,10)    (10,10)
   *
   */
  @Test
  fun `polygon with holes`() {
    val polygon = PostgisPolygon("((0 0, 10 0, 10 10, 0 10, 0 0), (4 4, 4 8, 8 8, 8 4, 4 4))").toJtsGeometry<Polygon>()
    val outside = PostgisPoint("POINT(5 5)").toJtsGeometry<Point>()
    val inside = PostgisPoint("POINT(0 0)").toJtsGeometry<Point>()
    assertThat(polygon.intersects(outside)).isEqualTo(false)
    assertThat(polygon.intersectsWith(outside)).isEqualTo(false)
    assertThat(polygon.intersects(inside)).isEqualTo(true)
    assertThat(polygon.intersectsWith(inside)).isEqualTo(true)
  }
}
