package com.alext.rtree.core.geometry.standard

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.core.geometry.math.Distance
import com.alext.rtree.core.geometry.math.Intersect
import kotlin.math.max
import kotlin.math.min

data class DefaultRectangle(
  override val x1: Double,
  override val y1: Double,
  override val x2: Double,
  override val y2: Double
) : Rectangle {

  override val mbr: Rectangle = this
  override val geometry: Geometry = this

  override fun merge(rectangle: Rectangle): Rectangle {
    return Geometries.rectangle(
      min(x1, rectangle.x1),
      min(y1, rectangle.y1),
      max(x2, rectangle.x2),
      max(y2, rectangle.y2)
    )
  }

  override fun contains(x: Double, y: Double): Boolean {
    val w = x2 - x1
    val h = y2 - y1
    return x1 <= x && x <= (x1 + w) && y1 <= y && y <= (y1 + h)
  }

  override fun intersects(rectangle: Rectangle): Boolean {
    return Intersect.test(x1, y1, x2, y2, rectangle.x1, rectangle.y1, rectangle.x2, rectangle.y2)
  }

  override fun distance(rectangle: Rectangle): Double {
    return Distance.between(x1, y1, x2, y2, rectangle)
  }

  override fun intersectionArea(rectangle: Rectangle): Double {
    return if (!intersects(rectangle)) {
      0.0
    } else {
      Geometries.rectangle(
        max(x1, rectangle.x1),
        max(y1, rectangle.y1),
        min(x2, rectangle.x2),
        min(y2, rectangle.y2)
      ).area()
    }
  }

  override fun perimeter(): Double {
    return 2 * (x2 - x1) + 2 * (y2 - y1)
  }

  override fun area(): Double {
    return (x2 - x1) * (y2 - y1)
  }
}
