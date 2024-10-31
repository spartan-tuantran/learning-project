package com.alext.redis.core

interface KeyCombiner {

  val keys: Set<Keyspace<*>>

  fun stamp(vararg value: Any): String {
    return keys.joinToString("") { it.key } + value.joinToString(":") { it.toString() }
  }

  companion object {

    fun from(
      vararg keyspace: Keyspace<*>
    ): KeyCombiner {
      return object : KeyCombiner {
        override val keys: Set<Keyspace<*>> = keyspace.toSet()
      }
    }
  }
}
