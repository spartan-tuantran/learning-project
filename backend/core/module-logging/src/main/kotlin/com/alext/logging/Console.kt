package com.alext.logging

const val ANSI_RESET = "\u001B[0m"
const val ANSI_BLACK = "\u001B[30m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_BLUE = "\u001B[34m"
const val ANSI_PURPLE = "\u001B[35m"
const val ANSI_CYAN = "\u001B[36m"
const val ANSI_WHITE = "\u001B[37m"

object Console {

  fun black(message: Any?) {
    println(ANSI_BLACK + message + ANSI_RESET)
  }

  fun black(msg: () -> Any?) {
    black(msg.toStringSafe())
  }

  fun white(message: Any?) {
    println(ANSI_WHITE + message + ANSI_RESET)
  }

  fun white(msg: () -> Any?) {
    white(msg.toStringSafe())
  }

  fun cyan(message: Any?) {
    println(ANSI_CYAN + message + ANSI_RESET)
  }

  fun cyan(msg: () -> Any?) {
    cyan(msg.toStringSafe())
  }

  fun purple(message: Any?) {
    println(ANSI_PURPLE + message + ANSI_RESET)
  }

  fun purple(msg: () -> Any?) {
    purple(msg.toStringSafe())
  }

  fun green(message: Any?) {
    println(ANSI_GREEN + message + ANSI_RESET)
  }

  fun green(msg: () -> Any?) {
    green(msg.toStringSafe())
  }

  fun red(message: Any?) {
    println(ANSI_RED + message + ANSI_RESET)
  }

  fun red(msg: () -> Any?) {
    red(msg.toStringSafe())
  }

  fun blue(message: Any?) {
    println(ANSI_BLUE + message + ANSI_RESET)
  }

  fun blue(msg: () -> Any?) {
    blue(msg.toStringSafe())
  }

  fun yellow(message: Any?) {
    println(ANSI_YELLOW + message + ANSI_RESET)
  }

  fun yellow(msg: () -> Any?) {
    yellow(msg.toStringSafe())
  }
}

interface ConsoleLogging {

  fun black(message: Any?) {
    println(ANSI_BLACK + message + ANSI_RESET)
  }

  fun black(msg: () -> Any?) {
    black(msg.toStringSafe())
  }

  fun white(message: Any?) {
    println(ANSI_WHITE + message + ANSI_RESET)
  }

  fun white(msg: () -> Any?) {
    white(msg.toStringSafe())
  }

  fun cyan(message: Any?) {
    println(ANSI_CYAN + message + ANSI_RESET)
  }

  fun cyan(msg: () -> Any?) {
    cyan(msg.toStringSafe())
  }

  fun purple(message: Any?) {
    println(ANSI_PURPLE + message + ANSI_RESET)
  }

  fun purple(msg: () -> Any?) {
    purple(msg.toStringSafe())
  }

  fun green(message: Any?) {
    println(ANSI_GREEN + message + ANSI_RESET)
  }

  fun green(msg: () -> Any?) {
    green(msg.toStringSafe())
  }

  fun red(message: Any?) {
    println(ANSI_RED + message + ANSI_RESET)
  }

  fun red(msg: () -> Any?) {
    red(msg.toStringSafe())
  }

  fun blue(message: Any?) {
    println(ANSI_BLUE + message + ANSI_RESET)
  }

  fun blue(msg: () -> Any?) {
    blue(msg.toStringSafe())
  }

  fun yellow(message: Any?) {
    println(ANSI_YELLOW + message + ANSI_RESET)
  }

  fun yellow(msg: () -> Any?) {
    yellow(msg.toStringSafe())
  }
}
