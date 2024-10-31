package com.alext.rtree.core.geometry

import com.alext.rtree.core.geometry.standard.DefaultCircle
import com.alext.rtree.core.geometry.standard.DefaultLine
import com.alext.rtree.core.geometry.standard.DefaultPoint
import com.alext.rtree.core.geometry.standard.DefaultRectangle

object Geometries {
  /**
   * Create a point from a pair of coordinates
   *
   * @param x The x coordinate
   * @param y The y coordinate
   */
  fun point(x: Double, y: Double): Point {
    return DefaultPoint(x, y)
  }

  /**
   * Create a minimum bounding box from 2 points
   *
   * @param x1 The left x coordinate
   * @param y1 The top y coordinate
   * @param x2 The right x coordinate
   * @param y2 The bottom y coordinate
   */
  fun rectangle(x1: Double, y1: Double, x2: Double, y2: Double): Rectangle {
    return DefaultRectangle(x1, y1, x2, y2)
  }

  /**
   * Create a circle from a center and radius
   *
   * @param x The center x coordinate
   * @param y The center y coordinate
   * @param radius The radius of circle
   */
  fun circle(x: Double, y: Double, radius: Double): Circle {
    return DefaultCircle(x, y, radius)
  }

  /**
   * Create a line from 2 points
   *
   * @param x1 The left x coordinate
   * @param y1 The left y coordinate
   * @param x2 The right x coordinate
   * @param y2 The right y coordinate
   */
  fun line(x1: Double, y1: Double, x2: Double, y2: Double): Line {
    return DefaultLine(x1, y1, x2, y2)
  }

  /**
   * Returns the minimum bounding rectangle of a number of items.
   *
   * @param items items to bound
   * @return the minimum bounding rectangle containing items
   */
  fun mbr(items: Collection<HasGeometry>): Rectangle {
    var minX1 = Double.MAX_VALUE
    var minY1 = Double.MAX_VALUE
    var maxX2 = Double.MIN_VALUE
    var maxY2 = Double.MIN_VALUE
    for (item in items) {
      val r = item.geometry.mbr
      if (r.x1 < minX1) {
        minX1 = r.x1
      }
      if (r.y1 < minY1) {
        minY1 = r.y1
      }
      if (r.x2 > maxX2) {
        maxX2 = r.x2
      }
      if (r.y2 > maxY2) {
        maxY2 = r.y2
      }
    }
    return rectangle(minX1, minY1, maxX2, maxY2)
  }
}
