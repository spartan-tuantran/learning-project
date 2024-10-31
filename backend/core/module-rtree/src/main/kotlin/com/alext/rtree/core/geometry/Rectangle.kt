package com.alext.rtree.core.geometry

import com.alext.rtree.document.Notes

/**
 * A rectangle that formed by 2 points, top left and bottom right
 */
@Notes("all implementation must implement equals() and hashCode()")
interface Rectangle : Geometry, HasGeometry {
  val x1: Double
  val y1: Double
  val x2: Double
  val y2: Double
  fun area(): Double
  fun perimeter(): Double
  fun intersectionArea(rectangle: Rectangle): Double
  fun merge(rectangle: Rectangle): Rectangle
  fun contains(x: Double, y: Double): Boolean
}

fun Rectangle.width() = x2 - x1
fun Rectangle.height() = y2 - y1
