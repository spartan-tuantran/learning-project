package com.alext.redis.config

data class RedisClientConfig(
  val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
  val minIdle: Int = DEFAULT_MAX_TOTAL,
  val maxIdle: Int = DEFAULT_MAX_TOTAL,
  val maxTotal: Int = DEFAULT_MAX_TOTAL
) {

  companion object {
    const val DEFAULT_MAX_TOTAL = 100
    const val DEFAULT_MAX_ATTEMPTS = 3
  }
}
