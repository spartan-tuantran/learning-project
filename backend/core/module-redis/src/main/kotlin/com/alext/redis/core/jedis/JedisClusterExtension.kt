package com.alext.redis.core.jedis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisCluster
import redis.clients.jedis.params.ScanParams

fun JedisCluster.flushAll(
  interceptor: ((String) -> Unit)? = null
): Set<String> {
  try {
    val params = ScanParams().count(Int.MAX_VALUE)
    val keys = mutableSetOf<String>()
    clusterNodes.forEach { (_, pool) ->
      Jedis(pool.resource).use { jedis ->
        var cursor = ScanParams.SCAN_POINTER_START
        do {
          val result = jedis.scan(cursor, params)
          keys.addAll(result.result)
          cursor = result.cursor
        } while (cursor != ScanParams.SCAN_POINTER_START)
      }
    }
    return keys.also {
      keys.forEach { key ->
        interceptor?.invoke(key)
        del(key)
      }
    }
  } catch (e: Exception) {
    e.printStackTrace()
    return emptySet()
  }
}
