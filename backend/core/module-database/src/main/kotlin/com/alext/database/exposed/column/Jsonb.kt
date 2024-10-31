package com.alext.database.exposed.column

import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

class JsonColumnType<out T : Any>(
  private val klass: Class<T>,
  private val parser: JsonParser,
  override var nullable: Boolean
) : ColumnType() {

  override fun sqlType() = "jsonb"

  override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
    stmt[index] = PGobject().apply {
      this.type = sqlType()
      this.value = value as String?
    }
  }

  override fun valueFromDB(value: Any) = when (value) {
    is PGobject -> parser.parse(klass, value.value!!)
    is Map<*, *> -> value
    else -> throw IllegalArgumentException("Unexpected value type ${value::class}")
  }

  @Suppress("UNCHECKED_CAST")
  override fun notNullValueToDB(value: Any) = parser.stringify(klass, value as T)

  @Suppress("UNCHECKED_CAST")
  override fun nonNullValueToString(value: Any) = "'${parser.stringify(klass, value as T)}'"
}

class JsonKey(val key: String) : Expression<String>() {
  init {
    if (!key.matches(Regex("\\w+"))) {
      throw IllegalArgumentException("Only simple json key allowed. Found '$key'")
    }
  }

  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder { append(key) }
}

class JsonValue<T>(
  private val expression: Expression<*>,
  private val key: JsonKey,
  override val columnType: IColumnType
) : Function<T>(columnType) {

  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
    append("CAST((${expression.toQueryBuilder(queryBuilder)} ->> '${key.key}') AS ${columnType.sqlType()})")
  }
}

inline fun <reified T : Any> Table.jsonb(
  name: String,
  parser: JsonParser,
  nullable: Boolean
): Column<T> {
  return registerColumn(name, JsonColumnType(T::class.java, parser, nullable))
}

inline fun <reified T> Column<Map<*, *>>.json(key: JsonKey): Function<T> {
  val columnType = when (T::class) {
    Int::class -> IntegerColumnType()
    String::class -> TextColumnType()
    Boolean::class -> BooleanColumnType()
    else -> throw RuntimeException("Column type ${T::class} not supported for json field.")
  }
  return JsonValue(this, key, columnType)
}

interface JsonParser {
  fun <T> parse(klass: Class<T>, string: String): T
  fun <T> stringify(klass: Class<T>, value: T): String
}

class JacksonJsonParser(
  private val objectMapper: ObjectMapper = ObjectMapper()
) : JsonParser {
  override fun <T> parse(klass: Class<T>, string: String) = objectMapper.readValue(string, klass)!!
  override fun <T> stringify(klass: Class<T>, value: T) = objectMapper.writeValueAsString(value)!!
}
