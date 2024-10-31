package com.alext.database.utility

object Address {

  fun build(
    addressLine1: String?,
    addressLine2: String?,
    city: String?,
    state: String?,
    zip: String?,
    country: String?
  ): String {
    val stateName = if (state != null && zip != null) "${state.trim()} ${zip.trim()}" else null
    return listOfNotNull(addressLine1, addressLine2, city, stateName, country)
      .map { it.trim() }
      .filter { it.isNotEmpty() }
      .joinToString(", ")
      .trim()
  }
}
