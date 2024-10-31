package com.alext.postgresql.extension

import java.util.UUID
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.FloatColumnType
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.UUIDColumnType

private const val JSONB = "JSONB"

class JsonbColumnType : ColumnType() {
  override fun sqlType() = JSONB
}

class JsonValue<T>(
  private val expr: Expression<*>,
  override val columnType: ColumnType,
  private val jsonPath: List<String>
) : Function<T>(columnType) {
  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
    val castJson = columnType.sqlType() != JSONB
    if (castJson) append("(")
    append(expr)
    append(" #>")
    if (castJson) append(">")
    append(" '{${jsonPath.joinToString { escapeFieldName(it) }}}'")
    if (castJson) append(")::${columnType.sqlType()}")
  }

  companion object {
    private fun escapeFieldName(value: String) = value.map {
      fieldNameCharactersToEscape[it] ?: it
    }.joinToString("").let { "\"$it\"" }

    private val fieldNameCharactersToEscape = mapOf(
      '\"' to "\\\"",
      '\r' to "\\r",
      '\n' to "\\n"
    )
  }
}

inline fun <reified T> Column<*>.json(vararg jsonPath: String): JsonValue<T> {
  val columnType = when (T::class) {
    Boolean::class -> BooleanColumnType()
    Int::class -> IntegerColumnType()
    Long::class -> LongColumnType()
    Double::class -> DoubleColumnType()
    Float::class -> FloatColumnType()
    String::class -> TextColumnType()
    UUID::class -> UUIDColumnType()
    else -> JsonbColumnType()
  }

  return JsonValue(this, columnType, jsonPath.toList())
}
