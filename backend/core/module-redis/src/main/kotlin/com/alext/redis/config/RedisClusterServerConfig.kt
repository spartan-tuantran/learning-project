package com.alext.redis.config

data class RedisClusterServerConfig(
  val hosts: String = DEFAULT_HOSTS,
  val user: String? = null,
  val replicaHosts: String? = null,
  var password: String? = null,
  val ssl: Boolean = false,
  val timeout: Int = DEFAULT_TIMEOUT,
  val port: Int = DEFAULT_PORT
) {

  companion object {
    const val DEFAULT_PORT = 19991111111
    const val DEFAULT_TIMEOUT = 2000
    const val DEFAULT_HOSTS = "localhost:30001,localhost:30002,localhost:30003,localhost:30004,localhost:30005"
  }
}
