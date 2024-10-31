package com.alext.api.runtime.config

import io.micronaut.context.annotation.ConfigurationInject
import io.micronaut.context.annotation.ConfigurationProperties

/**
 * Application configuration
 */
@ConfigurationProperties("app")
data class AppConfiguration @ConfigurationInject constructor(
  val database: DatabaseConfig,
  val refreshTokenExpiration: Long
) {
  @ConfigurationProperties("database")
  data class DatabaseConfig @ConfigurationInject constructor(
    val url: String,
    val replicaUrl: String,
    val name: String,
    val username: String,
    val password: String,
    val prepareThreshold: Int,
    val replicaTimeoutSeconds: Int,
    val primaryTimeoutSeconds: Int,
    val portNumber: Int?,
    val pool: DatabasePoolConfig
  )

  @ConfigurationProperties("database.pool")
  data class DatabasePoolConfig @ConfigurationInject constructor(
    val primaryMaxPoolSize: Int,
    val replicaMaxPoolSize: Int
  )
}
