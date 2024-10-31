package com.alext.database.utility

object Version {

  fun isPrimaryVersion(version: String): Boolean {
    val regex = """^\d+(\.\d+)*$""".toRegex()
    return regex.matches(version)
  }

  fun extractPrimaryVersion(version: String): String {
    val regex = """^(\d+\.\d+\.\d+).*?""".toRegex()
    val matchResult = regex.find(version)
    return matchResult?.groupValues?.get(1) ?: ""
  }
}
