package com.alext.redis.core

import com.alext.jackson.configured
import com.fasterxml.jackson.databind.ObjectMapper

internal object RedisJackson {
  /**
   * A singleton instance of [ObjectMapper] for all internal JSON operations
   */
  val SINGLETON = ObjectMapper().configured()
}
