package com.alext.rtree.core.geometry

import com.alext.rtree.document.Notes

/**
 * A line that contains 2 points
 */
@Notes("all implementation must implement equals() and hashCode()")
interface Line : Geometry {
  val x1: Double
  val y1: Double
  val x2: Double
  val y2: Double
  fun intersects(line: Line): Boolean
  fun intersects(point: Point): Boolean
  fun intersects(circle: Circle): Boolean
}
