package com.alext.redis.lock

import java.util.UUID
import kotlin.concurrent.Volatile

import redis.clients.jedis.JedisCluster

internal class JedisClusterLock(
  private val client: JedisCluster,
  private val key: String,
  private val waitTimeMillis: Long = DEFAULT_ACQUIRE_TIMEOUT_MILLIS,
  private val leaseTimeMillis: Long = DEFAULT_EXPIRY_TIME_MILLIS
) {

  companion object {
    private const val ONE_SECOND = 1000L
    const val DEFAULT_EXPIRY_TIME_MILLIS = 60 * ONE_SECOND
    const val DEFAULT_ACQUIRE_TIMEOUT_MILLIS = 10 * ONE_SECOND
    const val DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100

    // Note: index of key&argv starts from 1
    private const val COMMAND_LOCK = "if (redis.call('exists', KEYS[1]) == 0) then " +
      "redis.call('hset', KEYS[1], ARGV[1], 1); " +
      "redis.call('pexpire', KEYS[1], ARGV[2]); " +
      "return 1; " +
      "end; " +
      "if (redis.call('hexists', KEYS[1], ARGV[1]) == 1) then " +
      "local counter = redis.call('hincrby', KEYS[1], ARGV[1], 1); " +
      "redis.call('pexpire', KEYS[1], ARGV[2]); " +
      "return counter; " +
      "end; " +
      "return nil;"
    private const val COMMAND_UNLOCK = "if (redis.call('hexists', KEYS[1], ARGV[1]) == 0) then " +
      "return nil;" +
      "end; " +
      "local counter = redis.call('hincrby', KEYS[1], ARGV[1], -1); " +
      "if (counter > 0) then " +
      "redis.call('pexpire', KEYS[1], ARGV[2]); " +
      "return counter; " +
      "else " +
      "redis.call('del', KEYS[1]); " +
      "return 0; " +
      "end; " +
      "return nil;"
    private const val COMMAND_RENEW = "if (redis.call('hexists', KEYS[1], ARGV[1]) == 1) then " +
      "redis.call('pexpire', KEYS[1], ARGV[2]); " +
      "return 1; " +
      "end; " +
      "return nil;"
  }

  val id: String = UUID.randomUUID().toString() + "-" + Thread.currentThread().id

  /**
   * Lock threads counter
   *
   * @return counter
   */
  @Volatile
  var counter: Long = 0
    private set

  /**
   * Acquire lock.
   *
   * @return true if lock is acquired, false acquire timeout
   */
  fun acquire(): Boolean {
    var timeout = waitTimeMillis
    while (timeout >= 0) {
      val result: Any? = client.eval(COMMAND_LOCK, 1, key, id, leaseTimeMillis.toString() + "")
      if (result == null) {
        timeout -= DEFAULT_ACQUIRE_RESOLUTION_MILLIS
        try {
          Thread.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS.toLong())
        } catch (e: InterruptedException) {
          // Do nothing
        }
      } else {
        counter = result as Long
        return true
      }
    }
    return false
  }

  /**
   * Renew lock.
   *
   * @return true if lock is acquired, false otherwise
   */
  fun renew(): Boolean {
    val result = client.eval(COMMAND_RENEW, 1, key, id, leaseTimeMillis.toString() + "")
    return result != null
  }

  /**
   * Acquired lock release.
   */
  fun release() {
    val result = client.eval(COMMAND_UNLOCK, 1, key, id, leaseTimeMillis.toString() + "")
    counter = if (result == null) {
      0
    } else {
      result as Long
    }
  }
}
