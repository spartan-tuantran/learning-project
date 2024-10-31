package com.alext.redis.core.jedis

import com.alext.redis.config.RedisClusterServerConfig
import com.alext.redis.config.RedisServerConfig
import com.alext.redis.config.toHostPorts
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.Connection
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisCluster
import redis.clients.jedis.JedisPooled

object JedisFactory {

  fun pooled(
    config: RedisServerConfig
  ): JedisPooled {
    val user = config.user ?: ""
    val password = config.password ?: ""
    val hosts = config.hosts.takeIf { it.isNotEmpty() } ?: RedisServerConfig.DEFAULT_HOSTS
    return if (user.isNotEmpty() && password.isNotEmpty()) {
      JedisPooled(
        HostAndPort(hosts, config.port),
        DefaultJedisClientConfig
          .builder()
          .user(user)
          .password(password)
          .ssl(config.ssl)
          .timeoutMillis(RedisServerConfig.DEFAULT_TIMEOUT)
          .build(),
        GenericObjectPoolConfig<Connection>().apply {
          maxTotal = 100
          maxIdle = 100
          minIdle = 100
        }
      )
    } else {
      JedisPooled(
        config.hosts,
        config.port
      )
    }
  }

  fun cluster(
    config: RedisClusterServerConfig
  ): JedisCluster {
    val hosts = config.hosts.takeIf { it.isNotEmpty() } ?: RedisClusterServerConfig.DEFAULT_HOSTS
    println("-----hosts: $hosts")

    return JedisCluster(
      hosts.toHostPorts(),
      DefaultJedisClientConfig
        .builder()
        .apply {
          val user = config.user ?: ""
          val password = config.password ?: ""
          if (user.isNotEmpty()) {
            user(user)
          }
          if (password.isNotEmpty()) {
            password(password)
          }
        }
        .ssl(config.ssl)
        .timeoutMillis(RedisClusterServerConfig.DEFAULT_TIMEOUT)
        .build(),
      3,
      GenericObjectPoolConfig<Connection>().apply {
        maxTotal = 100
        maxIdle = 100
        minIdle = 100
      }
    )
  }
}
