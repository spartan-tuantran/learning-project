package com.alext.redis.core.jedis

import com.alext.redis.config.RedisClusterServerConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

// FIXME: make tests work in Github Actions pipeline
@EnabledIfEnvironmentVariable(named = "test", matches = "local")
class JedisClusterExtensionTest {

  companion object {
    private val cluster = JedisFactory.cluster(RedisClusterServerConfig())
  }

  @Test
  fun `flushAll should remove all keys`() {
    assertThat(cluster.set("k1", "v1")).isEqualTo("OK")
    assertThat(cluster.set("k2", "v2")).isEqualTo("OK")
    cluster.flushAll()
    assertThat(cluster.get("k1")).isEqualTo(null)
    assertThat(cluster.get("k2")).isEqualTo(null)
  }
}
