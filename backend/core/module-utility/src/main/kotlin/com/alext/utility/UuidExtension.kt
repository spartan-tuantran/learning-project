package com.alext.utility

import java.util.UUID

/**
 * Utility to convert from String unique to UUID.
 */
fun uuid(objectId: String): UUID {
  return UUID.nameUUIDFromBytes(objectId.toByteArray())
}

/**
 * Utility to convert from list of String unique to UUID List.
 */
fun uuidList(objectIds: List<String>): List<UUID> {
  return objectIds.map { uuid(it) }
}
