package com.alext.redis.core.redisson

import com.alext.logging.logger
import com.alext.logging.warn
import com.alext.redis.core.Keyspace
import com.alext.redis.core.KeyspaceAware
import com.alext.redis.core.RedisJackson
import com.alext.redis.error.RedisCommandException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.TimeUnit
import org.redisson.api.RedissonClient

/**
 * An adapter for [RedissonClient] with namespace awareness to avoid cache key collisions.
 * This interface extends [KeyspaceAware] to ensure each cache key is unique to its namespace.
 *
 * It provides a set of convenient methods to interact with Redis, including operations like
 * set, get, and delete, with support for serialization and deserialization using Jackson.
 */
interface ManagedRedisson : KeyspaceAware {
  val client: RedissonClient
  val jackson: ObjectMapper

  /**
   * Generates a namespaced key for Redis operations.
   *
   * @param value The values to be included in the key.
   * @return A string representing the namespaced key.
   */
  fun keyFrom(vararg value: Any): String {
    return keyspace.stamp(*value)
  }

  /**
   * Executes a Redis operation wrapped in a try-catch block. Returns null if an exception occurs.
   *
   * @param T The return type of the Redis operation.
   * @param block The Redis operation to execute.
   * @return The result of the Redis operation, or null if an exception is caught.
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
   * Stores an entity in Redis under a specified key with an expiration time.
   *
   * @param E The entity's type.
   * @param key The key under which to store the entity.
   * @param entity The entity to store.
   * @param expirationSeconds The time in seconds after which the key will expire.
   */
  fun <E : Any> set(key: Any, entity: E, expirationSeconds: Long) {
    client
      .getBucket<String>(keyFrom(key))
      .set(jackson.writeValueAsString(entity), expirationSeconds, TimeUnit.SECONDS)
  }

  /**
   * Stores an entity in Redis under a specified key.
   *
   * @param E The entity's type.
   * @param key The key under which to store the entity.
   * @param entity The entity to store.
   */
  fun <E : Any> set(key: Any, entity: E) {
    client
      .getBucket<String>(keyFrom(key))
      .set(jackson.writeValueAsString(entity))
  }

  /**
   * Retrieves an entity from Redis based on a specified key and type.
   *
   * @param E The entity's type.
   * @param key The key to retrieve the entity from.
   * @param type The class of the type to deserialize to.
   * @return The entity if found, or null otherwise.
   */
  fun <E : Any> get(key: Any, type: Class<E>): E? {
    try {
      return client
        .getBucket<String>(keyFrom(key))
        .get()
        ?.let { json ->
          jackson.readValue(json, type)
        }
    } catch (e: Exception) {
      if (e is JsonParseException) {
        logger.warn("Failed to parse Redis value of [key=$key, class=${type.simpleName}] due to: ${e.message}. Clean up Redis key for prefetch from DB.")
        delete(key)
        return null
      }

      throw RedisCommandException("Failed to get value of [key=$key, class=${type.simpleName}]", e)
    }
  }

  /**
   * Deletes an entity from Redis based on a specified key.
   *
   * @param key The key to delete.
   * @return True if the operation was successful, false otherwise.
   */
  fun delete(key: Any): Boolean {
    return client.getBucket<String>(keyFrom(key)).delete()
  }

  /**
   * Clean up resources acquired by Redisson and release connections to Redis
   */
  fun stop() {
    client.shutdown()
  }

  companion object {

    private val logger = ManagedRedisson::class.java.logger()

    /**
     * Creates and returns an instance of [ManagedRedisson].
     *
     * This function is a factory method for creating a new [ManagedRedisson] object with a specified Redisson client and namespace.
     * It ensures that each ManagedRedisson instance is initialized with its own client and namespace configuration.
     *
     * @param client The [RedissonClient] instance to be used by the ManagedRedisson.
     * @param namespace The [Keyspace] instance representing the namespace for the keys used in Redis operations.
     * @return An instance of [ManagedRedisson] configured with the given Redisson client and namespace.
     */
    fun create(
      client: RedissonClient,
      namespace: Keyspace<*>,
      jackson: ObjectMapper? = null
    ): ManagedRedisson {
      return object : ManagedRedisson {
        override val keyspace: Keyspace<*> = namespace
        override val jackson: ObjectMapper = jackson ?: RedisJackson.SINGLETON
        override val client: RedissonClient = client
      }
    }
  }
}
