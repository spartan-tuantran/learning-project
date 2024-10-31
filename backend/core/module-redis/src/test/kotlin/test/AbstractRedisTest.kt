package test

import com.alext.redis.config.RedisClusterServerConfig
import com.alext.redis.core.jedis.JedisFactory

abstract class AbstractRedisTest {

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
