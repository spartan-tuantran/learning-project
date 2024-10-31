package com.alext.health


import com.alext.database.runtime.DatabaseContext
import com.alext.logging.logger
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.health.HealthStatus
import io.micronaut.management.endpoint.health.HealthEndpoint
import io.micronaut.management.health.indicator.AbstractHealthIndicator
import io.micronaut.management.health.indicator.HealthIndicator
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * A Health Indicator for SQL Database.
 * Config Property: health.database.enabled
 */
@Singleton
@Requires(classes = [HealthIndicator::class])
@Requires(
  property = HealthEndpoint.PREFIX + "." + DatabaseHealthIndicator.NAME + ".enabled",
  defaultValue = StringUtils.TRUE
)
class DatabaseHealthIndicator(
  private val dbContext: DatabaseContext
) : AbstractHealthIndicator<Map<String, Any>>() {

  companion object {
    /**
     * Default name to use for health indication for database.
     */
    const val NAME = "database"
    const val QUERY = "SELECT 1"
    private val logger = DatabaseHealthIndicator::class.java.logger()
  }

  override fun getName(): String {
    return NAME
  }

  override fun getHealthInformation(): Map<String, Any> {
    val databases = mapOf("primary" to this.dbContext.primary, "replica" to this.dbContext.replica)
    val detail = mutableMapOf<String, Any>()
    var isHealthy = true

    databases.forEach { (kind, db) ->
      try {
        transaction(db) {
          exec(QUERY) { rs ->
            rs.next()
            val result = rs.getInt(1)
            if (result == 1) {
              detail[kind] = "healthy"
            } else {
              throw Exception("Result of '$QUERY' query is not valid")
            }
          }
        }
      } catch (e: Exception) {
        isHealthy = false
        val message = "Database $kind is not healthy - error: ${e.message ?: "Unknown error"}"
        detail[kind] = message
        logger.warn(message)
      }
    }

    healthStatus = if (isHealthy) {
      HealthStatus.UP
    } else {
      HealthStatus.DOWN
    }

    return detail
  }
}
