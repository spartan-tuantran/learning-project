package com.alext.redis.config

import redis.clients.jedis.HostAndPort

fun String.toHostPorts(defaultPort: Int = RedisServerConfig.DEFAULT_PORT): Set<HostAndPort> {
  return parseHostPorts(defaultPort)
    .map { HostAndPort(it.first, it.second) }
    .toSet()
}

fun String.parseHostPorts(defaultPort: Int = RedisServerConfig.DEFAULT_PORT): List<Pair<String, Int>> {
  return split(",")
    .asSequence()
    .map { it.split(":") }
    .map {
      val host = it[0]
      require(host.isNotBlank()) {
        "Host cannot be empty: $this."
      }
      val port = try {
        it.getOrNull(1)?.toInt() ?: defaultPort
      } catch (e: Exception) {
        throw IllegalArgumentException("Port is not a valid number: $this.")
      }
      Pair(host, port)
    }
    .toList()
}
