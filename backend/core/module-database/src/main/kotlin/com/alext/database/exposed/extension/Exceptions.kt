package com.alext.database.exposed.extension

import org.jetbrains.exposed.exceptions.ExposedSQLException

fun ExposedSQLException.conflictOn(index: String): Boolean {
  return message?.contains("duplicate key value violates unique constraint \"$index\"") == true
}
