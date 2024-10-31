package com.alext.redis

import com.alext.redis.core.jedis.flushAll as jedisFlushAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.AbstractRedisTest

class RedisPlayground : AbstractRedisTest() {

  @Test
  fun `test redis cluster`() {
    val key = "key"
    val value = "hello"
    redisCluster.set(key, value)
    assertThat(redisCluster.get(key)).isEqualTo(value)
  }

  @Test
  fun `flush all`() {
    redisCluster.jedisFlushAll()
  }

  @Test
  fun `redis hash`() {
    redisCluster.hset(
      "object",
      mapOf(
        "k1" to "1",
        "k2" to "2"
      )
    )
    println(redisCluster.hmget("object", "k1"))
    println(redisCluster.hmget("object", "k2"))
  }
}
