package com.alext.api.events

import com.alext.logging.logger
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Singleton

@Singleton
class MainApplicationStartupEventListener : ApplicationEventListener<ApplicationStartupEvent> {

  companion object {
    private val logger = MainApplicationStartupEventListener::class.logger()
  }

  override fun onApplicationEvent(event: ApplicationStartupEvent) {
    logger.info("application starts")
  }
}
