package com.alext.logging

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.slf4j.Logger

class MockLoggingDefaultEnableAllLogLevelTest : Logging {
  private val logger = mockk<Logger>(relaxed = true)

  @Test
  fun test() {
    val logMessage = "hello"

    debug { logMessage }
    verify(exactly = 1) { logger.debug(logMessage) }

    info { logMessage }
    verify(exactly = 1) { logger.info(logMessage) }

    warn { logMessage }
    verify(exactly = 1) { logger.warn(logMessage) }

    error { logMessage }
    verify(exactly = 1) { logger.error(logMessage) }
  }

  override fun logger(): Logger {
    return logger
  }
}

class InfoLogDisabledTest : Logging {
  private val logger = mockk<Logger>(relaxed = true)

  @Test
  fun test() {
    val logMessage = "hello"

    info { logMessage }
    verify(exactly = 0) { logger.info(logMessage) }

    warn { logMessage }
    verify(exactly = 1) { logger.warn(logMessage) }

    error { logMessage }
    verify(exactly = 1) { logger.error(logMessage) }

    debug { logMessage }
    verify(exactly = 1) { logger.debug(logMessage) }
  }

  override fun toggle(): Toggle {
    return Toggle().apply {
      info = false
    }
  }

  override fun logger(): Logger {
    return logger
  }
}

class WarnLogDisabledTest : Logging {
  private val logger = mockk<Logger>(relaxed = true)

  @Test
  fun test() {
    val logMessage = "hello"

    warn { logMessage }
    verify(exactly = 0) { logger.warn(logMessage) }

    error { logMessage }
    verify(exactly = 1) { logger.error(logMessage) }

    debug { logMessage }
    verify(exactly = 1) { logger.debug(logMessage) }

    info { logMessage }
    verify(exactly = 1) { logger.info(logMessage) }
  }

  override fun toggle(): Toggle {
    return Toggle().apply {
      warn = false
    }
  }

  override fun logger(): Logger {
    return logger
  }
}

class ErrorLogDisabledTest : Logging {
  private val logger = mockk<Logger>(relaxed = true)

  @Test
  fun test() {
    val logMessage = "hello"

    error { logMessage }
    verify(exactly = 0) { logger.error(logMessage) }

    debug { logMessage }
    verify(exactly = 1) { logger.debug(logMessage) }

    info { logMessage }
    verify(exactly = 1) { logger.info(logMessage) }

    warn { logMessage }
    verify(exactly = 1) { logger.warn(logMessage) }
  }

  override fun toggle(): Toggle {
    return Toggle().apply {
      error = false
    }
  }

  override fun logger(): Logger {
    return logger
  }
}

class DebugLogDisabledTest : Logging {
  private val logger = mockk<Logger>(relaxed = true)

  @Test
  fun test() {
    val logMessage = "hello"

    debug { logMessage }
    verify(exactly = 0) { logger.debug(logMessage) }

    info { logMessage }
    verify(exactly = 1) { logger.info(logMessage) }

    warn { logMessage }
    verify(exactly = 1) { logger.warn(logMessage) }

    error { logMessage }
    verify(exactly = 1) { logger.error(logMessage) }
  }

  override fun toggle(): Toggle {
    return Toggle().apply {
      debug = false
    }
  }

  override fun logger(): Logger {
    return logger
  }
}

class ExceptionLogTest : Logging {
  private val logger = mockk<Logger>(relaxed = true)

  @Test
  fun test() {
    val e = Exception("unknown exception")
    val logMessage = "hello"
    error(e) { logMessage }
    verify(exactly = 1) { logger.error(logMessage, e) }

    warn(e) { logMessage }
    verify(exactly = 1) { logger.warn(logMessage, e) }

    info(e) { logMessage }
    verify(exactly = 1) { logger.info(logMessage, e) }

    debug(e) { logMessage }
    verify(exactly = 1) { logger.debug(logMessage, e) }
  }

  override fun logger(): Logger {
    return logger
  }
}

@EnabledIfEnvironmentVariable(named = "DEV_TEST", matches = "true")
class DefaultLoggingDevTest : Logging {
  @Test
  fun test() {
    val logMessage = "hello"

    debug { logMessage }
    info { logMessage }
    warn { logMessage }
    error { logMessage }
  }
}
