package com.alext.rtree.core.geometry.standard

import com.alext.rtree.core.geometry.Circle
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Line
import com.alext.rtree.core.geometry.Point
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.core.geometry.math.Distance
import com.alext.rtree.math.Maths
import kotlin.math.max
import kotlin.math.sqrt

internal data class DefaultCircle(
  override val x: Double,
  override val y: Double,
  override val radius: Double
) : Circle {

  override val mbr: Rectangle = Geometries.rectangle(x - radius, y - radius, x + radius, y + radius)

  override fun distance(rectangle: Rectangle): Double {
    return max(0.0, Distance.between(x, y, rectangle) - radius)
  }

  override fun intersects(rectangle: Rectangle): Boolean {
    return distance(rectangle) == 0.0
  }

  override fun intersects(circle: Circle): Boolean {
    val total = radius + circle.radius
    return Distance.square(x, y, circle.x, circle.y) <= total * total
  }

  override fun intersects(point: Point): Boolean {
    return sqrt(Maths.sqr(x - point.x) + Maths.sqr(y - point.y)) <= radius
  }

  override fun intersects(line: Line): Boolean {
    return line.intersects(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Circle) return false
    if (x != other.x) return false
    if (y != other.y) return false
    if (radius != other.radius) return false
    return true
  }

  override fun hashCode(): Int {
    var result = x.hashCode()
    result = 31 * result + y.hashCode()
    result = 31 * result + radius.hashCode()
    return result
  }

  override fun toString(): String {
    return "Circle[x=$x, y=$y, radius=$radius]"
  }
}
