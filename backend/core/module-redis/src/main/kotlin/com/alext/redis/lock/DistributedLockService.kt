package com.alext.redis.lock

import com.alext.redis.core.KeyspaceAware

interface DistributedLockService : KeyspaceAware {

  /**
   * Tries to acquire the lock with defined [leaseTimeMillis]
   * Waits up to defined [waitTimeMillis] if necessary until the lock became available.
   * Lock will be released automatically after defined [leaseTimeMillis] interval.
   *
   * @param waitTimeMillis the maximum time to acquire the lock, in milliseconds
   * @param leaseTimeMillis The lease time, in milliseconds
   */
  fun <T> acquire(
    key: Any,
    waitTimeMillis: Long,
    leaseTimeMillis: Long,
    onSuccess: () -> T,
    onFailure: (e: Exception?) -> T
  ): T

  fun keyFrom(vararg value: Any): String {
    return keyspace.stamp(*value)
  }
}
