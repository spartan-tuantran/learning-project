package com.alext.logging

import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Class<*>.logger(): Logger {
  return LoggerFactory.getLogger(this)
}

fun KClass<*>.logger(): Logger {
  return LoggerFactory.getLogger(this.java)
}

fun Logger.warn(msg: () -> Any?) {
  warn(msg.toStringSafe())
}

fun Logger.warn(t: Throwable, msg: () -> Any?) {
  warn(msg.toStringSafe(), t)
}

fun Logger.info(msg: () -> Any?) {
  info(msg.toStringSafe())
}

fun Logger.info(t: Throwable, msg: () -> Any?) {
  info(msg.toStringSafe(), t)
}

fun Logger.error(msg: () -> Any?) {
  error(msg.toStringSafe())
}

fun Logger.error(t: Throwable, msg: () -> Any?) {
  error(msg.toStringSafe(), t)
}

fun Logger.debug(t: Throwable, msg: () -> Any?) {
  debug(msg.toStringSafe(), t)
}

fun Logger.debug(msg: () -> Any?) {
  debug(msg.toStringSafe())
}

fun Logger.trace(msg: () -> Any?) {
  trace(msg.toStringSafe())
}

fun Logger.trace(t: Throwable, msg: () -> Any?) {
  trace(msg.toStringSafe(), t)
}

@Suppress("NOTHING_TO_INLINE")
inline fun (() -> Any?).toStringSafe(): String {
  return try {
    invoke().toString()
  } catch (e: Exception) {
    e.printStackTrace().toString()
  }
}
