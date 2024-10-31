package com.alext.logging

import org.slf4j.Logger

/**
 * Core logging interface with the ability to attach logging method to each class without
 * having to declare any class member.
 *
 * Allow child classes to optionally toggle on/of log at different level.
 */
interface Logging {
  val delegate: Logger get() = object : Logger by logger() {
    override fun isErrorEnabled(): Boolean {
      return toggle().error
    }

    override fun isDebugEnabled(): Boolean {
      return toggle().debug
    }

    override fun isInfoEnabled(): Boolean {
      return toggle().info
    }

    override fun isWarnEnabled(): Boolean {
      return toggle().warn
    }

    override fun isTraceEnabled(): Boolean {
      return toggle().trace
    }
  }

  /**
   * Use this method to override delegate logger then toggle log level
   * By default all logs are enabled.
   *
   * @param toggle The abstract logging toggle to be overridden
   */
  fun toggle(): Toggle {
    return Toggle()
  }

  /**
   * The actual logger implementation.
   * Use the default logger implementation at Logger.kt
   * @return The logger instance
   */
  fun logger(): Logger {
    return javaClass.logger()
  }

  fun info(msg: () -> Any?) {
    if (delegate.isInfoEnabled) {
      delegate.info(msg)
    }
  }

  fun info(t: Throwable, msg: () -> Any?) {
    if (delegate.isInfoEnabled) {
      delegate.info(t, msg)
    }
  }

  fun warn(msg: () -> Any?) {
    if (delegate.isWarnEnabled) {
      delegate.warn(msg)
    }
  }

  fun warn(t: Throwable, msg: () -> Any?) {
    if (delegate.isWarnEnabled) {
      delegate.warn(t, msg)
    }
  }

  fun error(msg: () -> Any?) {
    if (delegate.isErrorEnabled) {
      delegate.error(msg)
    }
  }

  fun error(t: Throwable, msg: () -> Any?) {
    if (delegate.isErrorEnabled) {
      delegate.error(t, msg)
    }
  }

  fun debug(msg: () -> Any?) {
    if (delegate.isDebugEnabled) {
      delegate.debug(msg)
    }
  }

  fun debug(t: Throwable, msg: () -> Any?) {
    if (delegate.isDebugEnabled) {
      delegate.debug(t, msg)
    }
  }
}
