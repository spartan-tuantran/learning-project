package com.alext.rtree.core.geometry

import com.alext.rtree.document.Notes

/**
 * A circle with center and radius
 */
@Notes("all implementation must implement equals() and hashCode()")
interface Circle : Geometry {
  val x: Double
  val y: Double
  val radius: Double
  fun intersects(circle: Circle): Boolean
  fun intersects(point: Point): Boolean
  fun intersects(line: Line): Boolean
}
