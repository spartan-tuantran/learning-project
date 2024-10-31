package com.alext.redis.core.jedis

import com.alext.logging.logger
import com.alext.logging.warn
import com.alext.redis.core.Keyspace
import com.alext.redis.core.KeyspaceAware
import com.alext.redis.core.RedisJackson
import com.fasterxml.jackson.databind.ObjectMapper
import redis.clients.jedis.JedisCluster
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.UnifiedJedis

/**
 * An adapter for [JedisCluster] but with namespace awareness to avoid cache key collision
 */
interface ManagedJedis<J : UnifiedJedis> : KeyspaceAware {
  val jedis: J
  val jackson: ObjectMapper

  fun keyFrom(vararg value: Any): String {
    return keyspace.stamp(*value)
  }

  /**
   * Convenient method to wrap a redis call [block] and return null
   */
  fun <T> guard(block: () -> T?): T? {
    return try {
      block()
    } catch (e: Exception) {
      logger.warn(e) {
        "Failed to cache to Redis due to=${e.message}"
      }
      null
    }
  }

  /**
   * Convenient method to write entity class as JSON
   *
   * @param key The raw key, will be sanitized via [key] method
   * @param entity A generic entity of type [E]
   * @param expirationSeconds The TTL value in seconds for this key
   */
  fun <E : Any> set(key: Any, entity: E, expirationSeconds: Long): String {
    return jedis.setex(keyFrom(key), expirationSeconds, jackson.writeValueAsString(entity))
  }

  /**
   * Convenient to read entity as JSON
   *
   * @param key The raw key, will be sanitized via [key] method
   * @param type The Java class type which is used by [ObjectMapper] to deserialize JSON
   */
  fun <E : Any> get(key: Any, type: Class<E>): E? {
    return jedis.get(keyFrom(key))?.let { json ->
      jackson.readValue(json, type)
    }
  }

  /**
   * Convenient to delete a key
   *
   * @param key The raw key, will be sanitized via [key] method
   */
  fun delete(key: Any): Long {
    return jedis.del(keyFrom(key))
  }

  fun incrBy(key: Any, amount: Long): Long {
    return jedis.incrBy(keyFrom(key), amount)
  }

  fun addToSet(key: Any, value: String): Long {
    return jedis.sadd(keyFrom(key), value)
  }

  fun removeFromSet(key: Any, value: String): Long {
    return jedis.srem(keyFrom(key), value)
  }

  fun membersOfSet(key: Any): Set<String> {
    return jedis.smembers(keyFrom(key))
  }

  companion object {

    private val logger = ManagedJedis::class.java.logger()

    fun cluster(
      redis: JedisCluster,
      namespace: Keyspace<*>
    ): ManagedJedis<JedisCluster> {
      return object : ManagedJedis<JedisCluster> {
        override val keyspace: Keyspace<*> = namespace
        override val jackson: ObjectMapper = RedisJackson.SINGLETON
        override val jedis: JedisCluster = redis
      }
    }

    fun single(
      redis: JedisPooled,
      namespace: Keyspace<*>
    ): ManagedJedis<JedisPooled> {
      return object : ManagedJedis<JedisPooled> {
        override val keyspace: Keyspace<*> = namespace
        override val jackson: ObjectMapper = RedisJackson.SINGLETON
        override val jedis: JedisPooled = redis
      }
    }
  }
}
