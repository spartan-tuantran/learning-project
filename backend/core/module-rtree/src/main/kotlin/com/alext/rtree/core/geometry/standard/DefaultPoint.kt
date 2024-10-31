package com.alext.rtree.core.geometry.standard

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Point
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.core.geometry.math.Distance
import kotlin.math.max
import kotlin.math.min

internal data class DefaultPoint(
  override val x: Double,
  override val y: Double
) : Point {

  override val mbr: Rectangle = this
  override val geometry: Geometry = this
  override val x1: Double = x
  override val y1: Double = y
  override val x2: Double = x
  override val y2: Double = y

  override fun distance(rectangle: Rectangle): Double {
    return Distance.between(x, y, rectangle)
  }

  override fun intersects(rectangle: Rectangle): Boolean {
    return rectangle.x1 <= x && x <= rectangle.x2 && rectangle.y1 <= y && y <= rectangle.y2
  }

  override fun area(): Double {
    return 0.0
  }

  override fun merge(rectangle: Rectangle): Rectangle {
    return Geometries.rectangle(
      min(x, rectangle.x1),
      min(y, rectangle.y1),
      max(x, rectangle.x2),
      max(y, rectangle.y2)
    )
  }

  override fun contains(x: Double, y: Double): Boolean {
    return this.x == x && this.y == y
  }

  override fun intersectionArea(rectangle: Rectangle): Double {
    return 0.0
  }

  override fun perimeter(): Double {
    return 0.0
  }

  override fun toString(): String {
    return "Point[x=$x, y=$y]"
  }
}
