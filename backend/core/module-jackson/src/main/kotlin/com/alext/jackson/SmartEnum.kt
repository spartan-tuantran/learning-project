package com.alext.jackson

data class SmartEnum<E : Enum<E>>(
  val value: E?,
  val raw: String = (value as Enum<*>).toString()
) {
  override fun toString() = raw

  override fun equals(other: Any?): Boolean {
    if (other !is SmartEnum<*>) {
      return false
    }
    if (value == null) {
      return raw == other.raw
    }
    return value == other.value
  }

  override fun hashCode(): Int {
    return value?.hashCode() ?: raw.hashCode()
  }
}

fun <E : Enum<E>> E.asSmartEnum(): SmartEnum<E> {
  return SmartEnum(this)
}
