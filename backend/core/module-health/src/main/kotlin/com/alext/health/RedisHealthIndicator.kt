package com.alext.health

import com.alext.logging.logger
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.health.HealthStatus
import io.micronaut.management.endpoint.health.HealthEndpoint
import io.micronaut.management.health.indicator.AbstractHealthIndicator
import io.micronaut.management.health.indicator.HealthIndicator
import jakarta.inject.Singleton
import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNodes

/**
 * A Health Indicator for Redis.
 * Config Property: health.redis.enabled
 */
@Suppress("MnInjectionPoints")
@Singleton
@Requires(classes = [HealthIndicator::class])
@Requires(
  property = HealthEndpoint.PREFIX + "." + RedisHealthIndicator.NAME + ".enabled",
  defaultValue = StringUtils.TRUE
)
class RedisHealthIndicator(
  private val redisson: RedissonClient
) : AbstractHealthIndicator<Map<String, Any>>() {

  companion object {
    /**
     * Default name to use for health indication for Redis.
     */
    const val NAME = "redis"
    private val logger = RedisHealthIndicator::class.java.logger()
  }

  override fun getName(): String {
    return NAME
  }

  override fun getHealthInformation(): Map<String, Any> {
    val redisNodes = redisson.getRedisNodes(RedisNodes.CLUSTER)

    val allNodeCount = redisNodes.masters.size + redisNodes.slaves.size
    val upNodeCount = redisNodes.masters.count { master ->
      master.ping()
    } + redisNodes.slaves.count { slave ->
      slave.ping()
    }

    val detail = mutableMapOf<String, Any>("healthy_nodes" to upNodeCount)

    // If all nodes are down, then the health status is DOWN
    healthStatus = if (upNodeCount != allNodeCount) {
      val errorMessage = "${allNodeCount - upNodeCount} nodes in Redis cluster is down"
      detail["error"] = errorMessage
      logger.warn("Redis Health-check: $errorMessage")
      HealthStatus.DOWN
    } else {
      HealthStatus.UP
    }
    return detail
  }
}
