package com.alext.database.runtime

import com.alext.database.config.ConnectionPoolConfig
import com.alext.database.config.DatabaseConfig
import com.alext.database.config.primary
import com.alext.database.config.replica
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.Properties
import javax.sql.DataSource
import org.postgis.PGbox2d
import org.postgis.PGbox3d
import org.postgis.PGgeometry
import org.postgresql.PGConnection

internal interface DataSourceFactory {
  /**
   * Create a new instance of [DataSource] using [database] and [pool] config
   *
   * @param kind The database kind which is either "primary" or "replica"
   * @param database The main database config
   * @param pool The connection pool config
   */
  fun create(kind: DatabaseKind, database: DatabaseConfig, pool: ConnectionPoolConfig): DataSource

  companion object {

    val HIKARI = object : DataSourceFactory {
      override fun create(kind: DatabaseKind, database: DatabaseConfig, pool: ConnectionPoolConfig): DataSource {
        val (properties, poolNameSuffix) = when (kind) {
          DatabaseKind.PRIMARY -> Pair(database.primary, "primary")
          DatabaseKind.REPLICA -> Pair(database.replica, "replica")
        }
        return HikariDataSource(
          pool.hikariConfig(properties).apply {
            this.poolName = "hikari-${database.name}-$poolNameSuffix"
          }
        ).apply {
          connection.use { c ->
            c.unwrap(PGConnection::class.java).apply {
              addDataType("geometry", PGgeometry::class.java)
              addDataType("box3d", PGbox3d::class.java)
              addDataType("box2d", PGbox2d::class.java)
            }
          }
        }
      }

      /**
       * A quick note transaction isolation levels:
       * TRANSACTION_READ_COMMITTED
       *
       * - Lower Isolation Level:
       *   Allows other transactions to modify data that a transaction has previously read.
       *
       * - Reduced Locking Overhead:
       *   Since it doesn't need to maintain the same snapshot of data for the duration of the transaction, it typically requires fewer locks or uses locks for shorter durations.
       *   This can result in increased concurrency, allowing more transactions to execute simultaneously.
       *
       * - Risk of Non-Repeatable Reads:
       *   It accepts the possibility of non-repeatable reads but prevents dirty reads.
       *
       * - Use Case:
       *   Ideal for applications where absolute consistency for the duration of the transaction is not critical and where higher throughput and concurrency are desirable.
       */
      fun ConnectionPoolConfig.hikariConfig(properties: Properties): HikariConfig {
        val config = HikariConfig(properties)
        config.connectionTimeout = connectionTimeout
        config.maximumPoolSize = maxPoolSize
        config.maxLifetime = maxLifetime
        config.minimumIdle = maxPoolSize
        config.idleTimeout = idleTimeout
        // See https://www.postgresql.org/docs/current/transaction-iso.html for more details

        config.transactionIsolation = "TRANSACTION_READ_COMMITTED"
        config.leakDetectionThreshold = 60_000
        return config
      }
    }
  }
}
