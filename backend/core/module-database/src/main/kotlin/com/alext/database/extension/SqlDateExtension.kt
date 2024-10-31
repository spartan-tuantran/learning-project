package com.alext.postgresql.extension

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType

fun <T : OffsetDateTime?> Expression<T>.toInstantColumn(): OffsetToInstantColumnFunction<T> =
  OffsetToInstantColumnFunction(this)

class OffsetToInstantColumnFunction<T : OffsetDateTime?>(
  private val expression: Expression<T>
) : Function<Instant>(JavaInstantColumnType()) {

  /**
   * Generates a SQL query by calling the [QueryBuilder] with the date conversion expression.
   * Appends the [expression] OffsetDateTime to the date conversion function.
   *
   * @return the SQL expression for this check
   */
  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
    append(expression)
    append(" AT TIME ZONE '${ZoneId.of("UTC")}'")
  }
}

/**
 * Checks whether the given [ZoneId] follows the ISO-8601 format for time zone offsets.
 * ISO-8601 offsets include patterns like "+08:00" (with or without seconds) or "-05:30" (with or without seconds).
 *
 * @param zoneId The [ZoneId] to check.
 *
 * @return `true` if the [ZoneId] follows the ISO-8601 offset format, `false` otherwise.
 */
fun ZoneId.isISO8601Offset(): Boolean {
  val id = this.id
  // Regular expression to match ISO-8601 offsets like "+08:00" or "-05:30"
  val iso8601OffsetRegex = """^[+-]\d{2}:\d{2}(:\d{2})?$""".toRegex()
  return iso8601OffsetRegex.matches(id)
}

/**
 * Create a SQL expression for grouping time values by date in a specified time zone.
 *
 * This function is used to generate a SQL query that groups date and time values.
 *
 * @param zoneId The time zone in which the date and time values should be interpreted.
 * @return A [TimeGroupByDate] instance representing the SQL expression.
 */
fun <T : OffsetDateTime?> Expression<T>.timeGroupByDate(zoneId: ZoneId): TimeGroupByDate<T> =
  TimeGroupByDate(this, zoneId)

/**
 * SQL expression for grouping date and time values by date in a specified time zone.
 */
class TimeGroupByDate<T : OffsetDateTime?>(
  private val expression: Expression<T>,
  private val zoneId: ZoneId
) : Function<String>(VarCharColumnType()) {

  /**
   * Generates a SQL query by calling the [QueryBuilder] with the date conversion expression.
   * Appends the [expression] expression to the date conversion function and specified timezone.
   *
   * @return the SQL expression for this check
   */
  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
    val interval = if (zoneId.isISO8601Offset()) "INTERVAL" else ""
    append("DATE(")
    append(expression)
    append(" AT TIME ZONE $interval '${zoneId.id}'")
    append(")")
  }
}
