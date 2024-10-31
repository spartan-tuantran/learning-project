package com.alext.database.exposed.extension

import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

fun Table.offsetDateTime(name: String): Column<OffsetDateTime> =
  registerColumn(name, OffsetDateTimeColumnType())

private class OffsetDateTimeColumnType : ColumnType() {
  override fun sqlType(): String = "TIMESTAMP WITH TIME ZONE"

  override fun valueFromDB(value: Any): Any {
    return when (value) {
      is Timestamp -> OffsetDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
      is OffsetDateTime -> value
      else -> error("Unexpected value of type=[${value::class.java.name}] for OffsetDateTime column")
    }
  }

  override fun notNullValueToDB(value: Any): Any {
    return when (value) {
      is OffsetDateTime -> Timestamp.valueOf(value.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime())
      else -> error("Unexpected value of type=[${value::class.java.name}] for OffsetDateTime column")
    }
  }

  override fun nonNullValueToString(value: Any): String {
    return when (value) {
      is OffsetDateTime -> "'${value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}'"
      else -> error("Unexpected value of type=[${value::class.java.name}] for OffsetDateTime column")
    }
  }
}
