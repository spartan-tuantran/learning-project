package com.alext.redis.core

/**
 * A keyspace is a set of keys that share a common prefix.
 * Notice that this interface is the same as [Enum] where name must be unique.
 */
interface Keyspace<E : Keyspace<E>> : Comparable<E> {
  /** The name of the keyspace or [Enum.name] */
  val name: String

  val key: String
    get() {
      return "${name.lowercase()}::"
    }

  fun stamp(vararg value: Any): String {
    return key + value.joinToString(":") { it.toString() }
  }
}
