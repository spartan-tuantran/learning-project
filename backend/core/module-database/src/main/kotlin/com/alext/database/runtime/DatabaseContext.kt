package com.alext.database.runtime

import com.alext.database.config.ConnectionPoolConfig
import com.alext.database.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import org.jetbrains.exposed.sql.transactions.TransactionManager

private typealias ExposedDatabaseConfig = org.jetbrains.exposed.sql.DatabaseConfig

interface DatabaseContext {
  val config: DatabaseConfig
  val primary: Database
  val replica: Database
  val primaryTimeout: Int
  val replicaTimeout: Int
  val analyticReplicaTimeout: Int

  class Builder(
    private val config: DatabaseConfig
  ) {

    private var transaction: (Database) -> TransactionManager = { ThreadLocalTransactionManager(it) }
    private var logger: SqlLogger? = null

    /**
     * Set transaction manager, default is thread-local
     */
    fun transaction(manager: (Database) -> TransactionManager = { ThreadLocalTransactionManager(it) }): Builder {
      this.transaction = manager
      return this
    }

    /**
     * Attach logger, default is no-ops
     */
    fun logger(logger: SqlLogger): Builder {
      this.logger = logger
      return this
    }

    /**
     * Build a [DatabaseContext] given connection pool configs
     *
     * @param primaryPoolConfig Connection pool config for primary
     * @param replicaPoolConfig Connection pool config for replica
     */
    fun build(
      primaryPoolConfig: ConnectionPoolConfig = ConnectionPoolConfig(),
      replicaPoolConfig: ConnectionPoolConfig = ConnectionPoolConfig()
    ): DatabaseContext {
      val factory = DataSourceFactory.HIKARI
      val config = this.config
      val primary = factory.connect(DatabaseKind.PRIMARY, config, primaryPoolConfig)
      val replica = if (config.url != config.replicaUrl) {
        factory.connect(DatabaseKind.REPLICA, config, replicaPoolConfig)
      } else {
        primary
      }

      return object : DatabaseContext {
        override val config: DatabaseConfig = config
        override val primary = primary
        override val replica = replica
        override val primaryTimeout = config.primaryTimeoutSeconds
        override val replicaTimeout = config.replicaTimeoutSeconds
        override val analyticReplicaTimeout = config.analyticReplicaTimeoutSeconds
      }
    }

    private fun DataSourceFactory.connect(
      kind: DatabaseKind,
      db: DatabaseConfig,
      pool: ConnectionPoolConfig
    ): Database {
      return Database.connect(
        datasource = create(kind, db, pool),
        setupConnection = {},
        databaseConfig = logger?.let { l ->
          ExposedDatabaseConfig {
            sqlLogger = l
          }
        },
        manager = transaction
      )
    }
  }
}
