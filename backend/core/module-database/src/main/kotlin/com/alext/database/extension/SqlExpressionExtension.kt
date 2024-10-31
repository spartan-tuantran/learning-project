package com.alext.postgresql.extension

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ComparisonOp
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.QueryParameter
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Sum
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.append
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.sum

/**
 * Coalesce the values of a nullable Long column with 0 for null values.
 *
 * @return An ExpressionWithColumnType representing the coalesced column.
 */
fun Column<Long?>.coalesce(): ExpressionWithColumnType<Long> {
  return SqlExpressionBuilder.coalesce(this, longLiteral(0L))
}

/**
 * Sum the values of a nullable Long column after coalescing null values with 0.
 *
 * @return A Sum expression representing the sum of the coalesced column.
 */
fun Column<Long?>.sumCoalesce(): Sum<Long> {
  return SqlExpressionBuilder.coalesce(this, longLiteral(0L)).sum()
}

/**
 * Apply sorting criteria to the query based on the provided sorting criteria map.
 *
 * @param sortingCriteriaMap A map where the keys are SortDirection (ascending or descending) and values are columns.
 * @return The modified query with sorting criteria applied.
 */
fun Query.sorting(
  sortingCriteriaMap: Map<SortOrder?, ExpressionWithColumnType<out Any?>>
): Query {
  val query = this

  // Apply sorting criteria to the query
  sortingCriteriaMap.forEach { (sortOrder, column) ->
    sortOrder?.let {
      query.orderBy(column, it)
    }
  }

  return query
}

class JsonbAggFunction<T : Expression<*>>(
  private val expression: T
) : Function<String>(TextColumnType()) {

  /**
   * Generate a SQL query for aggregating JSON objects to array and return serialized string
   *
   * @param queryBuilder
   */
  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
    append("jsonb_agg(")
    append(expression)
    append(")::text")
  }
}

class JsonbBuildObjectFunction<T : Column<*>>(
  private val keyValues: Map<String, T>
) : Function<String>(TextColumnType()) {

  /**
   * Generates a SQL query for build JSON object from the key and value as expressions.
   *
   * @param queryBuilder
   */
  override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
    append("jsonb_build_object(")
    val params: MutableList<Any> = mutableListOf()
    keyValues.entries.forEach { (key, value) ->
      params.add("'$key'")
      params.add(",")
      params.add(value)
      params.add(",")
    }
    params.removeLast()
    append(*params.toTypedArray())
    append(")")
  }
}

/**
 * Using "ILIKE" operator of SQL.
 *
 * @return An [Op<Boolean>] representing Boolean Operator.
 */
class ILikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")
infix fun<T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> = ILikeOp(this, QueryParameter(pattern, columnType))
