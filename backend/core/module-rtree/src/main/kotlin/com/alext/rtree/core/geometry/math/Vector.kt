package com.alext.rtree.core.geometry.math

import kotlin.math.sqrt

/**
 * A 2D vector
 */
data class Vector(
  val x: Double,
  val y: Double
) {

  fun dot(v: Vector): Double {
    return x * v.x + y * v.y
  }

  operator fun times(value: Double): Vector {
    return Vector(value * x, value * y)
  }

  operator fun minus(v: Vector): Vector {
    return Vector(x - v.x, y - v.y)
  }

  fun modulus(): Double {
    return sqrt(modulusSquared())
  }

  fun modulusSquared(): Double {
    return x * x + y * y
  }

  override fun toString(): String {
    return "Vector[x=$x, y=$y]"
  }
}
