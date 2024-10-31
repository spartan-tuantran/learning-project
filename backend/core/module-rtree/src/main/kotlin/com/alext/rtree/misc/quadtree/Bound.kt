package com.alext.rtree.misc.quadtree

data class Bound(
  val minX: Double = Double.MIN_VALUE,
  val maxX: Double = Double.MAX_VALUE,
  val minY: Double = Double.MIN_VALUE,
  val maxY: Double = Double.MAX_VALUE
) {

  companion object {
    val WORLD = Bound()
  }

  val midX: Double = (minX + maxX) / 2
  val midY: Double = (minY + maxY) / 2

  fun contains(x: Double, y: Double): Boolean {
    return (x in minX..maxX) && (y in minY..maxY)
  }

  operator fun contains(point: Point): Boolean {
    return contains(point.x, point.y)
  }

  private fun intersects(minX: Double, maxX: Double, minY: Double, maxY: Double): Boolean {
    return minX < this.maxX && this.minX < maxX && minY < this.maxY && this.minY < maxY
  }

  fun intersects(bound: Bound): Boolean {
    return intersects(bound.minX, bound.maxX, bound.minY, bound.maxY)
  }

  operator fun contains(bound: Bound): Boolean {
    return bound.minX >= minX && bound.maxX <= maxX && bound.minY >= minY && bound.maxY <= maxY
  }

  fun quadrant(x: Double, y: Double): Int {
    return if (y < midY) {
      if (x < midX) {
        0 // top left
      } else {
        1 // top right
      }
    } else {
      if (x < midX) {
        2 // bottom left
      } else {
        3 // bottom right
      }
    }
  }
}
