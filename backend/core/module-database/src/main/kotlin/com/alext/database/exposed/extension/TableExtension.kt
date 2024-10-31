package com.alext.database.exposed.extension

import kotlin.reflect.full.memberProperties
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

inline fun <reified T : Table> T.columnOf(name: String?, default: Column<*>): Column<*> {
  return (T::class.memberProperties.firstOrNull { it.name == name }?.call(this) as? Column<*>) ?: default
}
