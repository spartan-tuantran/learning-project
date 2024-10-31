package com.alext.api.events

import com.alext.logging.logger
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationShutdownEvent
import jakarta.inject.Singleton

@Singleton
class MainApplicationShutdownEventListener : ApplicationEventListener<ApplicationShutdownEvent> {

  companion object {
    private val logger = MainApplicationShutdownEventListener::class.logger()
  }

  override fun onApplicationEvent(event: ApplicationShutdownEvent) {
    logger.info("application shutdowns")
  }
}
