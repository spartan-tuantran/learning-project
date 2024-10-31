package com.alext.rtree.algorithm.postgis

import com.alext.rtree.misc.jts.PostgisPoint
import com.alext.rtree.misc.jts.PostgisPolygon
import com.alext.rtree.misc.jts.centroid
import com.alext.rtree.misc.jts.intersects
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.postgis.Point
import test.AbstractRTreeTest
import test.Place

class RayCastingPolygonIntersectionAlgorithmTest : AbstractRTreeTest(), RayCastingPolygonIntersectionAlgorithm {

  @Test
  fun `point in polygon - use center`() {
    REGIONS.forEach { (_, zone) ->
      val polygon = zone.region
      val centroid = polygon.centroid()
      val point = Point(centroid.x, centroid.y)
      val expected = polygon.intersects(point)
      val actual = polygon.intersectsWith(point)
      assertThat(expected).isEqualTo(actual)
    }
  }

  @Test
  fun `point in polygon - use random points`() {
    REGIONS.forEach { (_, zone) ->
      val polygon = zone.region
      Place.values().forEach { place ->
        val point = PostgisPoint(place.lng, place.lat)
        val expected = polygon.intersects(point)
        val actual = polygon.intersectsWith(point)
        assertThat(expected).isEqualTo(actual)
      }
    }
  }

  @Test
  fun `polygon with just outer ring`() {
    val polygon = PostgisPolygon("POLYGON((50 35, 10 30, 10 10, 30 5, 45 20, 50 35))")
    val inside = PostgisPoint("POINT(30 20)")
    val outside = PostgisPoint("POINT(0 0)")
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
    val polygon = PostgisPolygon("((0 0, 10 0, 10 10, 0 10, 0 0), (4 4, 4 8, 8 8, 8 4, 4 4))")
    val outside = PostgisPoint("POINT(5 5)")
    val inside = PostgisPoint("POINT(0 0)")
    assertThat(polygon.intersects(outside)).isEqualTo(false)
    assertThat(polygon.intersectsWith(outside)).isEqualTo(false)
    assertThat(polygon.intersects(inside)).isEqualTo(true)
    assertThat(polygon.intersectsWith(inside)).isEqualTo(true)
  }
}
