package com.alext.redis.lock

import com.alext.logging.logger
import com.alext.redis.core.Keyspace
import java.util.concurrent.TimeUnit
import org.redisson.api.RedissonClient

interface RedissonDistributedLockService : DistributedLockService {

  val client: RedissonClient

  companion object {
    private val logger = RedissonDistributedLockService::class.java.logger()

    /**
     * Creates and returns an instance of [RedissonDistributedLockService].
     */
    fun create(
      client: RedissonClient,
      namespace: Keyspace<*>
    ): RedissonDistributedLockService {
      return object : RedissonDistributedLockService {
        override val client: RedissonClient = client
        override val keyspace: Keyspace<*> = namespace
      }
    }
  }

  override fun <T> acquire(
    key: Any,
    waitTimeMillis: Long,
    leaseTimeMillis: Long,
    onSuccess: () -> T,
    onFailure: (e: Exception?) -> T
  ): T {
    val keyWithNamespace = keyFrom(key)
    val lock = client.getLock(keyWithNamespace)
    val acquired = lock.tryLock(
      waitTimeMillis,
      leaseTimeMillis,
      TimeUnit.MILLISECONDS
    )
    return if (acquired) {
      try {
        logger.info("Acquired lock key=$keyWithNamespace")
        onSuccess.invoke()
      } finally {
        lock.unlock()
      }
    } else {
      logger.info("Lock key=$keyWithNamespace already acquired")
      onFailure.invoke(null)
    }
  }
}
