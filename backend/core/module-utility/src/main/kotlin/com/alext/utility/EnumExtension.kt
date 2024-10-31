package com.alext.utility

inline fun <reified T : Enum<T>> String?.asEnum(): T? {
  return try {
    this?.let {
      enumValueOf<T>(it.uppercase())
    }
  } catch (e: Exception) {
    null
  }
}

inline fun <reified T : Enum<T>> String?.asEnum(default: T): T {
  return try {
    this?.let {
      enumValueOf<T>(it.uppercase())
    } ?: default
  } catch (e: Exception) {
    default
  }
}
