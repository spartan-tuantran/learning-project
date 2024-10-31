package com.alext.rtree.core.geometry.math

import com.alext.rtree.core.geometry.Rectangle
import kotlin.math.max
import kotlin.math.sqrt

object Distance {

  fun square(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val dx = x2 - x1
    val dy = y2 - y1
    return dx * dx + dy * dy
  }

  fun between(x: Double, y: Double, rectangle: Rectangle): Double {
    return between(x, y, x, y, rectangle)
  }

  fun between(x1: Double, y1: Double, x2: Double, y2: Double, rectangle: Rectangle): Double {
    if (intersect(x1, y1, x2, y2, rectangle.x1, rectangle.y1, rectangle.x2, rectangle.y2)) {
      return 0.0
    }
    val xyMostLeft = x1 < rectangle.x1
    val mostLeftX1 = if (xyMostLeft) x1 else rectangle.x1
    val mostRightX1 = if (xyMostLeft) rectangle.x1 else x1
    val mostLeftX2 = if (xyMostLeft) x2 else rectangle.x2
    val xDifference = max(0.0, if (mostLeftX1 == mostRightX1) 0.0 else mostRightX1 - mostLeftX2)
    val xyMostDown = y1 < rectangle.y1
    val mostDownY1 = if (xyMostDown) y1 else rectangle.y1
    val mostUpY1 = if (xyMostDown) rectangle.y1 else y1
    val mostDownY2 = if (xyMostDown) y2 else rectangle.y2
    val yDifference = max(0.0, if (mostDownY1 == mostUpY1) 0.0 else mostUpY1 - mostDownY2)
    return sqrt(xDifference * xDifference + yDifference * yDifference)
  }

  private fun intersect(x1: Double, y1: Double, x2: Double, y2: Double, a1: Double, b1: Double, a2: Double, b2: Double): Boolean {
    return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2
  }
}
