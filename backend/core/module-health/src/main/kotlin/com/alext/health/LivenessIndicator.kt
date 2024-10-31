package com.alext.health

import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.AbstractHealthIndicator
import io.micronaut.management.health.indicator.annotation.Liveness
import jakarta.inject.Singleton

@Singleton
@Liveness
class LivenessIndicator : AbstractHealthIndicator<Boolean>() {

  companion object {
    private const val NAME = "liveness"
  }

  override fun getName(): String {
    return NAME
  }

  override fun getHealthInformation(): Boolean {
    healthStatus = HealthStatus.UP
    return true
  }
}
