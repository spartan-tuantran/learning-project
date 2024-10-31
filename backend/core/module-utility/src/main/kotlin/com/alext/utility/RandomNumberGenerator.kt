package com.alext.utility

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

object RandomNumberGenerator {

  fun randomLong(
    lowerBound: Long = 0L,
    upperBound: Long = 1000000L,
    random: Random = Random.Default
  ): Long {
    return random.nextLong(lowerBound, upperBound)
  }

  fun randomBigDecimal(
    lowerBound: Double = 0.0,
    upperBound: Double = 1000000.0,
    scale: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    random: Random = Random.Default
  ): BigDecimal {
    val randomDouble = random.nextDouble(lowerBound, upperBound)
    return BigDecimal(randomDouble).setScale(scale, roundingMode)
  }

  fun randomDouble(
    lowerBound: Double = 0.0,
    upperBound: Double = 100.0,
    random: Random = Random.Default
  ): Double {
    return random.nextDouble(lowerBound, upperBound)
  }
}
