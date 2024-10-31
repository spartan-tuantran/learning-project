package com.alext.api.runtime.factory

import com.alext.api.runtime.config.AppConfiguration
import com.alext.database.config.ConnectionPoolConfig
import com.alext.database.config.DatabaseConfig
import com.alext.database.runtime.DatabaseContext
import com.alext.postgresql.repository.DefaultRefreshTokenRepository
import com.alext.postgresql.repository.DefaultUserRepository
import com.alext.postgresql.repository.RefreshTokenRepository
import com.alext.postgresql.repository.UserRepository
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class DatabaseFactory {

  @Singleton
  fun provideDatabaseContext(
    config: AppConfiguration.DatabaseConfig
  ): DatabaseContext {
    return DatabaseContext
      .Builder(
        DatabaseConfig(
          url = config.url,
          replicaUrl = config.replicaUrl,
          name = config.name,
          username = config.username,
          password = config.password,
          prepareThreshold = config.prepareThreshold,
          replicaTimeoutSeconds = config.replicaTimeoutSeconds,
          primaryTimeoutSeconds = config.primaryTimeoutSeconds,
          portNumber = config.portNumber
        )
      )
      .build(
        primaryPoolConfig = ConnectionPoolConfig(
          maxPoolSize = config.pool.primaryMaxPoolSize
        ),
        replicaPoolConfig = ConnectionPoolConfig(
          maxPoolSize = config.pool.replicaMaxPoolSize
        )
      )
  }

  @Singleton
  fun provideUserRepository(
    db: DatabaseContext
  ): UserRepository {
    return DefaultUserRepository(db)
  }

  @Singleton
  fun provideRefreshTokenRepository(
    db: DatabaseContext
  ): RefreshTokenRepository {
    return DefaultRefreshTokenRepository(db)
  }
}
