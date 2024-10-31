package com.alext.database.config

import java.util.concurrent.TimeUnit

data class ConnectionPoolConfig(
  /**
   * This property controls the maximum number of milliseconds that a client (that's you) will wait for a connection
   * from the pool. If this time is exceeded without a connection becoming available, a SQLException will be thrown.
   * Lowest acceptable connection timeout is 250 ms. Default: 30000 (30 seconds)
   */
  val connectionTimeout: Long = TimeUnit.SECONDS.toMillis(30),

  /**
   * This property controls the maximum size that the pool is allowed to reach, including both idle and in-use
   * connections. Basically this value will determine the maximum number of actual connections to the database backend.
   * A reasonable value for this is best determined by your execution environment.
   * When the pool reaches this size, and no idle connections are available, calls to getConnection() will block for
   * up to connectionTimeout milliseconds before timing out. Please read about pool sizing. Default: 10
   */
  val maxPoolSize: Int = 2,

  /**
   * This property controls the minimum number of idle connections that HikariCP tries to maintain in the pool.
   * If the idle connections dip below this value and total connections in the pool are less than maximumPoolSize,
   * HikariCP will make a best effort to add additional connections quickly and efficiently.
   * However, for maximum performance and responsiveness to spike demands, we recommend not setting this value
   * and instead allowing HikariCP to act as a fixed size connection pool. Default: same as maximumPoolSize
   */
  val minimumIdle: Int = maxPoolSize,

  /**
   * This property controls the maximum lifetime of a connection in the pool. An in-use connection will never be
   * retired, only when it is closed will it then be removed. We strongly recommend setting this value, and it
   * should be at least 30 seconds less than any database or infrastructure imposed connection time limit.
   * A value of 0 indicates no maximum lifetime (infinite lifetime), subject of course to the idleTimeout setting.
   * Default: 1800000 (30 minutes)
   */
  val maxLifetime: Long = TimeUnit.MINUTES.toMillis(30),

  /**
   * This property controls the maximum amount of time that a connection is allowed to sit idle in the pool.
   * This setting only applies when minimumIdle is defined to be less than maximumPoolSize.
   * Whether a connection is retired as idle or not is subject to a maximum variation of +30 seconds,
   * and average variation of +15 seconds. A connection will never be retired as idle before this timeout.
   * A value of 0 means that idle connections are never removed from the pool.
   * The minimum allowed value is 10000ms (10 seconds). Default: 600000 (10 minutes)
   */
  val idleTimeout: Long = TimeUnit.MINUTES.toMillis(10)
)
