package com.alext.utility

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Rounds the current BigDecimal value to two decimal places using the HALF_DOWN rounding mode
 * and returns the result as a new BigDecimal instance.
 *
 * @return A new BigDecimal instance representing the rounded value.
 */
fun BigDecimal.roundToTwoDecimalPlaces(): BigDecimal {
  return this.setScale(2, RoundingMode.HALF_DOWN)
}

/**
 * Converts a nullable Long to a BigDecimal and rounds it to two decimal places.
 * If the input is null, it returns BigDecimal.ZERO.
 *
 * @return The converted and rounded BigDecimal value.
 */
fun Long?.roundToTwoDecimalPlaces(): BigDecimal {
  return this?.toBigDecimal()?.roundToTwoDecimalPlaces() ?: BigDecimal.ZERO
}

/**
 * Converts a nullable Double value to two decimal places using the HALF_DOWN rounding mode
 * If the input is null, it returns 0.00.
 *
 * @return A new Double instance representing the rounded value.
 */
fun Double?.roundToTwoDecimalPlaces(): Double {
  return this?.toBigDecimal()?.setScale(2, RoundingMode.HALF_DOWN)?.toDouble() ?: 0.00
}

fun BigDecimal.roundToLong(): Long {
  return this.setScale(0, RoundingMode.HALF_DOWN).toLong()
}
