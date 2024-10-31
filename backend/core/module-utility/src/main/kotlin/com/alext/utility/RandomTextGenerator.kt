package com.alext.utility

import kotlin.random.Random

object RandomTextGenerator {

  private val ALPHA_NUMERIC = ('a'..'z') + ('A'..'Z') + ('0'..'9')

  fun generate(
    length: Int,
    random: Random = Random.Default
  ): String {
    require(length in 1..10) {
      "Length must be between 1 and 10"
    }
    return ALPHA_NUMERIC.shuffled(random).take(length).joinToString("")
  }
}
