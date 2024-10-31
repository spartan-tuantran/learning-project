package com.alext.plugins.flyway.models

import java.io.File

data class SqlMigration(
  val order: Int,
  val fileName: String,
) {

  companion object {

    fun from(file: File): SqlMigration {
      val fileNameArray = file.name.toCharArray()
      val firstOfNonDigits = fileNameArray.indexOfFirst { !Character.isDigit(it) }
      val orderSubstring = file.name.substring(0, firstOfNonDigits)
      val asInt = orderSubstring.toIntOrNull() ?: 0
      return SqlMigration(asInt, file.name)
    }
  }
}
