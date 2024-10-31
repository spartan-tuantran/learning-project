package com.alext.rtree.math

import kotlin.math.abs
import kotlin.math.sqrt

object Maths {

  fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val dx = x2 - x1
    val dy = y2 - y1
    return sqrt(dx * dx + dy * dy)
  }

  fun mod(n: Double, m: Double): Double {
    val a = abs(m)
    val r = (n / a).toInt()
    var result = n - a * r
    if (result < 0) {
      result += a
    }
    return result
  }

  fun sqr(x: Double): Double {
    return x * x
  }
}
