package com.alext.rtree.algorithm.jts

import com.alext.rtree.algorithm.PolygonIntersectionAlgorithm
import com.alext.rtree.core.geometry.Line2D
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.PrecisionModel

/**
 * Using simple ray casting algorithm to check point is in polygon or not.
 */
interface RayCastingPolygonIntersectionAlgorithm : PolygonIntersectionAlgorithm<Polygon, Point> {

  companion object {
    private val FACTORY = GeometryFactory(PrecisionModel(PrecisionModel.FLOATING), 4326)

    /**
     * The starting point must be outside of polygon, thus using max (lng - 1) and (lat - 1).
     */
    private val START = FACTORY.createPoint(Coordinate(-181.0, -91.0))

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
    fun Array<Coordinate>.intersects(point: Point): Boolean {
      fun Point.intersects(l: Coordinate, r: Coordinate): Boolean {
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
      if (size < 3) {
        return false
      }

      // Loop through every 2 points in the polygon
      for (i in 0 until size - 1) {
        if (point.intersects(this[i], this[i + 1])) {
          count++
        }
      }

      // Last one with first one
      if (point.intersects(this[size - 1], this[0])) {
        count++
      }

      return count % 2 != 0
    }
  }

  override fun Polygon.intersectsWith(point: Point): Boolean {
    val exterior = exteriorRing.coordinates.intersects(point)
    if (numInteriorRing > 0) {
      (0 until numInteriorRing).forEach {
        val intersect = getInteriorRingN(it).coordinates.intersects(point)
        if (intersect) {
          return false
        }
      }
      return exterior
    } else {
      return exterior
    }
  }
}
