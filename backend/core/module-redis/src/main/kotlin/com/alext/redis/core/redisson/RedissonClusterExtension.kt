package com.alext.redis.core.redisson

import org.redisson.api.RedissonClient

fun RedissonClient.flushAll() {
  try {
    return keys.flushall()
  } catch (e: Exception) {
    // FIXME: log the error out properly
    e.printStackTrace()
  }
}
