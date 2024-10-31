package com.alext.postgresql.extension

import com.alext.database.view.FilterView
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder

/**
 * Paginates the query result and transforms it into a [FilterView].
 *
 * @param limit The maximum number of items to retrieve.
 * @param offset The starting index for paginating the result.
 * @param order The order expression.
 * @param converter A lambda function to transform a list of [ResultRow] to a list of type [E].
 * @param transform A lambda function to transform an individual [ResultRow] to type [E].
 * @return A [FilterView] containing the paginated and transformed result.
 * @throws IllegalArgumentException If both [transform] and [converter] are null.
 */
fun <E> Query.pagination(
  limit: Int,
  offset: Long,
  order: Pair<Expression<*>, SortOrder>? = null,
  converter: ((rows: List<ResultRow>) -> List<E>)? = null,
  transform: ((row: ResultRow) -> E)? = null
): FilterView<E> {
  val query = this
  val count = query.count()

  return if (count == 0L) {
    FilterView(0L, emptyList())
  } else {
    when {
      transform == null && converter == null -> {
        throw IllegalArgumentException("'pagination' requires at least one transform method")
      }

      else -> {
        val result = query
          .apply { order?.let { orderBy(it) } }
          .limit(limit, offset)
          .let { rowList ->
            when {
              transform != null -> rowList.map { transform(it) }
              converter != null -> converter(rowList.toList())
              else -> emptyList()
            }
          }

        FilterView(count, result)
      }
    }
  }
}
