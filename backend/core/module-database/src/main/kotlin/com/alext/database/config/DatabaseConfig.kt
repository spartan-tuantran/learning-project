package com.alext.database.config

import java.util.Properties

data class DatabaseConfig(
  val url: String = "localhost",
  val replicaUrl: String = "localhost",
  val name: String = "local",
  val username: String = "local",
  val password: String = "local",
  val prepareThreshold: Int = 0,
  val primaryTimeoutSeconds: Int = 10,
  val replicaTimeoutSeconds: Int = 10,
  val analyticReplicaTimeoutSeconds: Int = 10,
  val portNumber: Int? = 5432
)

internal val DatabaseConfig.primary: Properties
  get(): Properties {
    return properties(
      user = username,
      password = password,
      database = name,
      url = url,
      prepareThreshold = prepareThreshold,
      portNumber = portNumber
    )
  }

internal val DatabaseConfig.replica: Properties
  get(): Properties {
    return properties(
      user = username,
      password = password,
      database = name,
      url = replicaUrl,
      prepareThreshold = prepareThreshold,
      portNumber = portNumber
    )
  }

private fun properties(
  user: String,
  password: String,
  database: String,
  url: String,
  prepareThreshold: Int,
  portNumber: Int?
): Properties {
  return Properties().apply {
    setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    setProperty("dataSource.user", user)
    setProperty("dataSource.password", password)
    setProperty("dataSource.databaseName", database)
    setProperty("dataSource.serverName", url)
    setProperty("dataSource.prepareThreshold", prepareThreshold.toString())
    portNumber?.let {
      setProperty("dataSource.portNumber", it.toString())
    }
  }
}
