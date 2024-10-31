package com.alext.rtree.misc

import java.util.Random

fun Random.nextDouble(from: Double, to: Double): Double {
  val result = nextDouble() * (to - from) + from
  return result.coerceIn(from, to)
}
