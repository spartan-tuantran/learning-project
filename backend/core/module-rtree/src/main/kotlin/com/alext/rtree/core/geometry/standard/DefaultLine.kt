package com.alext.rtree.core.geometry.standard

import com.alext.rtree.core.geometry.Circle
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Line
import com.alext.rtree.core.geometry.Line2D
import com.alext.rtree.core.geometry.Point
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.core.geometry.math.Intersect
import com.alext.rtree.core.geometry.math.Vector
import kotlin.math.max
import kotlin.math.min

internal data class DefaultLine(
  override val x1: Double,
  override val y1: Double,
  override val x2: Double,
  override val y2: Double
) : Line {

  override val mbr: Rectangle = Geometries.rectangle(
    min(x1, x2),
    min(y1, y2),
    max(x1, x2),
    max(y1, y2)
  )

  override fun distance(rectangle: Rectangle): Double {
    if (rectangle.contains(x1, y1) || rectangle.contains(x2, y2)) {
      return 0.0
    } else {
      val d1 = distance(rectangle.x1, rectangle.y1, rectangle.x1, rectangle.y2)
      if (d1 == 0.0) {
        return 0.0
      }
      val d2 = distance(rectangle.x1, rectangle.y2, rectangle.x2, rectangle.y2)
      if (d2 == 0.0) {
        return 0.0
      }
      val d3 = distance(rectangle.x2, rectangle.y2, rectangle.x2, rectangle.y1)
      val d4 = distance(rectangle.x2, rectangle.y1, rectangle.x1, rectangle.y1)
      return min(d1, min(d2, min(d3, d4)))
    }
  }

  private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val line = Line2D(x1, y1, x2, y2)
    val d1 = line.distance(this.x1, this.y1)
    val d2 = line.distance(this.x2, this.y2)
    val line2 = Line2D(this.x1, this.y1, this.x2, this.y2)
    val d3 = line2.distance(x1, y1)
    if (d3 == 0.0) {
      return 0.0
    }
    val d4 = line2.distance(x2, y2)
    return if (d4 == 0.0) {
      0.0
    } else {
      min(d1, min(d2, min(d3, d4)))
    }
  }

  override fun intersects(rectangle: Rectangle): Boolean {
    return Intersect.test(rectangle, x1, y1, x2, y2)
  }

  override fun intersects(line: Line): Boolean {
    val line1 = Line2D(x1, y1, x2, y2)
    val line2 = Line2D(line.x1, line.y1, line.x2, line.y2)
    return line2.intersectsLine(line1)
  }

  override fun intersects(point: Point): Boolean {
    return intersects(point.mbr)
  }

  /**
   * Based on vector projection
   * https://en.wikipedia.org/wiki/Vector_projection
   */
  override fun intersects(circle: Circle): Boolean {
    val c = Vector(circle.x, circle.y)
    val a = Vector(x1, y1)
    val cMinusA = c.minus(a)
    val radiusSquared = circle.radius * circle.radius
    if (x1 == x2 && y1 == y2) {
      return cMinusA.modulusSquared() <= radiusSquared
    } else {
      val b = Vector(x2, y2)
      val bMinusA = b.minus(a)
      val bMinusAModulus = bMinusA.modulus()
      val lambda = cMinusA.dot(bMinusA) / bMinusAModulus
      // If projection is on the segment
      return if (lambda in 0.0..bMinusAModulus) {
        val dMinusA = bMinusA.times(lambda / bMinusAModulus)
        // calculate distance to line from c using pythagoras' theorem
        cMinusA.modulusSquared() - dMinusA.modulusSquared() <= radiusSquared
      } else {
        // return true if and only if an endpoint is within radius of
        // centre
        cMinusA.modulusSquared() <= radiusSquared || c.minus(b).modulusSquared() <= radiusSquared
      }
    }
  }
}
