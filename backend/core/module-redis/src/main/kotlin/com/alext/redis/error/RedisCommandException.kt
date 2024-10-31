package com.alext.redis.error

import java.lang.RuntimeException

class RedisCommandException(message: String?, cause: Throwable?) : RuntimeException(message, cause) {
  constructor(cause: Exception) : this("Redis command failed", cause)
}
