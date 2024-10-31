package com.alext.redis.core

/**
 * A contract that [ManagedJedis] agrees to implement to
 * avoid key collision.
 */
interface KeyspaceAware {
  val keyspace: Keyspace<*>
}
