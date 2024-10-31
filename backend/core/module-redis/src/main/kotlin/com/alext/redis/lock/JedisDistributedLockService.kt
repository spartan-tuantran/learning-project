package com.alext.redis.lock

import com.alext.logging.logger
import com.alext.redis.core.Keyspace
import redis.clients.jedis.JedisCluster

interface JedisDistributedLockService : DistributedLockService {

  val jedis: JedisCluster

  companion object {
    private val logger = JedisDistributedLockService::class.java.logger()

    /**
     * Creates and returns an instance of [JedisDistributedLockService].
     */
    fun create(
      jedis: JedisCluster,
      namespace: Keyspace<*>
    ): JedisDistributedLockService {
      return object : JedisDistributedLockService {
        override val jedis: JedisCluster = jedis
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
    val lock = JedisClusterLock(
      jedis,
      keyWithNamespace,
      waitTimeMillis = waitTimeMillis,
      leaseTimeMillis = leaseTimeMillis
    )
    return if (lock.acquire()) {
      try {
        onSuccess.invoke()
      } finally {
        lock.release()
      }
    } else {
      onFailure.invoke(null)
    }
  }
}
