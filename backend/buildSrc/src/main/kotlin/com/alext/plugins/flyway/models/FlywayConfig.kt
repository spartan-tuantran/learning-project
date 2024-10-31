package com.alext.plugins.flyway.models

import java.util.Properties
import org.gradle.api.GradleException

data class FlywayConfig(
  val dbName: String,
  val url: String,
  val user: String,
  val password: String
) {

  companion object {
    fun from(properties: Properties): FlywayConfig {
      val url =  properties.getProperty("flyway.url")
      val dbName = url.databaseName()

      return FlywayConfig(
        dbName = dbName,
        url = url,
        user = properties.getProperty("flyway.user"),
        password = properties.getProperty("flyway.password")
      )
    }

    private fun String.databaseName(): String {
      val parts = split("/")
      if (parts.size != 4) {
        throw GradleException("flyway.conf url should be of format jdbc:postgresql://<host>:<port>/<database>")
      }
      return parts.last()
    }
  }
}
