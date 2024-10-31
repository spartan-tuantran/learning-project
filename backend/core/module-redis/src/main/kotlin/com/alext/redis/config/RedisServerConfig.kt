package com.alext.redis.config

data class RedisServerConfig(
  val hosts: String = "localhost",
  val user: String? = null,
  val replicaHosts: String? = null,
  var password: String? = null,
  val ssl: Boolean = false,
  val timeout: Int = DEFAULT_TIMEOUT,
  val port: Int = DEFAULT_PORT
) {

  companion object {
    const val DEFAULT_PORT = 6379
    const val DEFAULT_TIMEOUT = 2000
    const val DEFAULT_HOSTS = "localhost"
  }
}
