package com.alext.rtree.core.geometry.math

import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.core.geometry.height
import com.alext.rtree.core.geometry.width
import kotlin.math.abs

private const val LEFT = 1 shl 0
private const val TOP = 2 shl 1
private const val RIGHT = 1 shl 2
private const val BOTTOM = 1 shl 3

object Intersect {

  fun test(x1: Double, y1: Double, x2: Double, y2: Double, a1: Double, b1: Double, a2: Double, b2: Double): Boolean {
    return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2
  }

  @Suppress("NAME_SHADOWING")
  fun test(r: Rectangle, x1: Double, y1: Double, x2: Double, y2: Double): Boolean {
    var x1 = x1
    var y1 = y1
    val rx = r.x1
    val ry = r.y1
    val width = r.width()
    val height = r.height()
    val segment = Line(x1, y1, x2, y2)
    if (r.on(segment)) {
      return true
    }
    val out2: Int = r.locate(x2, y2)
    if (out2 == 0) {
      return true
    }
    var out1: Int = r.locate(x1, y1)
    while (out1 != 0) {
      if (out1 and out2 != 0) {
        return false
      }
      if (out1 and (LEFT or RIGHT) != 0) {
        var x = rx
        if (out1 and RIGHT != 0) {
          x += width
        }
        y1 += (x - x1) * (y2 - y1) / (x2 - x1)
        x1 = x
      } else {
        var y = ry
        if (out1 and BOTTOM != 0) {
          y += height
        }
        x1 += (y - y1) * (x2 - x1) / (y2 - y1)
        y1 = y
      }
      out1 = r.locate(x1, y1)
    }
    return true
  }
}

data class Line(
  val x1: Double = 0.0,
  val y1: Double = 0.0,
  val x2: Double = 0.0,
  val y2: Double = 0.0
)

class Point(
  var x: Double = 0.0,
  var y: Double = 0.0
) {

  companion object {
    private const val PRECISION = 0.00000001
  }

  fun set(x: Double, y: Double): Point {
    this.x = x
    this.y = y
    return this
  }

  fun on(line: Line): Boolean {
    return if (x < line.x1 || x > line.x2 || y < line.y1 || y > line.y2) {
      false
    } else {
      val v = (line.y2 - line.y1) * (x - line.x1) - (line.x2 - line.x1) * (y - line.y1)
      abs(v) < PRECISION
    }
  }
}

fun Rectangle.on(segment: Line): Boolean {
  val x = x1
  val y = y1
  val width = width()
  val height = height()
  val point = Point(0.0, 0.0)
  return when {
    point.set(x, y).on(segment) -> true
    point.set(x + width, y).on(segment) -> true
    point.set(x, y + height).on(segment) -> true
    else -> point.set(x + width, y + height).on(segment)
  }
}

fun Rectangle.locate(x: Double, y: Double): Int {
  val rectX = x1
  val rectY = y1
  val width = width()
  val height = height()
  var out = 0
  when {
    width <= 0 -> out = out or (LEFT or RIGHT)
    x < rectX -> out = out or LEFT
    x > rectX + width -> out = out or RIGHT
  }
  when {
    height <= 0 -> out = out or (TOP or BOTTOM)
    y < rectY -> out = out or TOP
    y > rectY + height -> out = out or BOTTOM
  }
  return out
}
