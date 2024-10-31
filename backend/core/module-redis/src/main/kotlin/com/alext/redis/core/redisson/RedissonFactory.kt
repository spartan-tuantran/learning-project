package com.alext.redis.core.redisson

import com.alext.redis.config.RedisClusterServerConfig
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config

object RedissonFactory {

  fun cluster(
    config: RedisClusterServerConfig
  ): RedissonClient {
    val redisHosts = config.hosts.ifBlank { RedisClusterServerConfig.DEFAULT_HOSTS }
    val port = if (config.port != 0) config.port else RedisClusterServerConfig.DEFAULT_PORT

    return Redisson.create(
      Config().apply {
        useClusterServers().apply {
          // Redis cluster scan interval in milliseconds
          scanInterval = 5000
          connectTimeout = if (config.timeout != 0) config.timeout else RedisClusterServerConfig.DEFAULT_TIMEOUT
          nodeAddresses = redisHosts.split(",").map { toNodeAddress(it, port) }
          password = config.password?.ifEmpty { null }
          username = config.user?.ifEmpty { null }
          codec = StringCodec()
        }
      }
    )
  }

  private fun toNodeAddress(it: String, port: Int): String {
    val nodeAddress = if (it.contains(":")) it else "$it:$port"
    return "redis://${nodeAddress.trim()}"
  }
}
