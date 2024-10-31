package com.alext.jackson

import com.fasterxml.jackson.databind.ObjectMapper

@Suppress("UNCHECKED_CAST")
fun <T : Any> ObjectMapper.writeValueAsMap(any: T): Map<String, Any> {
  return convertValue(any, Map::class.java) as Map<String, Any>
}

inline fun <reified T : Any> ObjectMapper.readValue(map: Map<String, Any>): T {
  return convertValue(map, T::class.java)
}
