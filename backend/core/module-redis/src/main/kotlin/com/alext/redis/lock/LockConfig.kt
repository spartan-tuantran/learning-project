package com.alext.redis.lock

import java.time.Duration

data class LockConfig(
  val key: Any,

  // the maximum time to try to acquire a lock before giving up
  val waitTime: Duration,

  // the maximum time to hold a lock since it was granted
  val leaseTime: Duration
)
