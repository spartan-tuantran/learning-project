package com.alext.rtree.algorithm.postgis

import com.alext.rtree.algorithm.PolygonIntersectionAlgorithm
import com.alext.rtree.core.geometry.Line2D
import org.postgis.LinearRing
import org.postgis.Point
import org.postgis.Polygon

/**
 * Using simple ray casting algorithm to check point is in polygon or not.
 */
interface RayCastingPolygonIntersectionAlgorithm : PolygonIntersectionAlgorithm<Polygon, Point> {

  companion object {
    /**
     * The starting point must be outside of polygon, thus using max (lng - 1) and (lat - 1).
     */
    private val START = Point(-181.0, -91.0)

    /**
     * Check if a ring intersects with [point] using ray casting algorithm.
     * A polygon is considered to be intersected if the ray line intersects with
     * the polygon odd number of times and false otherwise.
     * Time complexity: O(n)
     *
     *  Outside, count = 0 (even)
     *       ******
     * o---o *    *
     *       *    *
     *       ******
     *
     * *  Outside, count = 2 (even)
     *     *******
     * o---*-----*---o
     *     *     *
     *     *******
     *
     * *  Inside, count = 1 (even)
     *     *******
     * o---*--o  *
     *     *     *
     *     *******
     */
    fun LinearRing.intersects(point: Point): Boolean {
      fun Point.intersects(l: Point, r: Point): Boolean {
        return Line2D.linesIntersect(
          START.x,
          START.y,
          x,
          y,
          l.x,
          l.y,
          r.x,
          r.y
        )
      }

      var count = 0
      val n = numPoints()
      if (numPoints() < 3) {
        return false
      }

      // Loop through every 2 points in the polygon
      for (i in 0 until n - 1) {
        if (point.intersects(getPoint(i), getPoint(i + 1))) {
          count++
        }
      }

      // Last one with first one
      if (point.intersects(getPoint(n - 1), getPoint(0))) {
        count++
      }

      return count % 2 != 0
    }
  }

  override fun Polygon.intersectsWith(point: Point): Boolean {
    val n = numRings()
    if (n < 1) {
      return false
    }
    val rings = (0 until numRings()).map { getRing(it) }
    if (n > 1) {
      (1 until n).forEach { i ->
        val intersect = rings[i].intersects(point)
        if (intersect) {
          return false
        }
      }
      return rings.first().intersects(point)
    } else {
      return rings.first().intersects(point)
    }
  }
}
