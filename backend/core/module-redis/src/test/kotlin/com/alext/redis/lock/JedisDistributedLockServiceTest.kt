package com.alext.redis.lock

import com.alext.redis.config.RedisClusterServerConfig
import com.alext.redis.core.KeyspaceTest
import com.alext.redis.core.jedis.JedisFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "test", matches = "local")
open class JedisDistributedLockServiceTest : DistributedLockServiceTest() {

  @BeforeEach
  fun beforeEach() {
    // This is required in order to clear Redis lock key
    redisCluster.flushAll()
  }

  override val lockService: DistributedLockService by lazy {
    JedisDistributedLockService.create(
      jedis = redisCluster,
      namespace = KeyspaceTest.TestKeyspace.HELLO_KOTLIN
    )
  }

  companion object {
    @JvmStatic
    protected val redisCluster by lazy {
      val hosts = if ((System.getenv("REDIS_HOSTS") != null) && (System.getenv("REDIS_HOSTS") != null)) {
        System.getenv("REDIS_HOSTS")
      } else {
        RedisClusterServerConfig.DEFAULT_HOSTS
      }

      JedisFactory.cluster(
        RedisClusterServerConfig(
          hosts = hosts,
          user = null,
          replicaHosts = null,
          password = null,
          ssl = false,
          timeout = RedisClusterServerConfig.DEFAULT_TIMEOUT,
          port = RedisClusterServerConfig.DEFAULT_PORT
        )
      )
    }
  }
}
