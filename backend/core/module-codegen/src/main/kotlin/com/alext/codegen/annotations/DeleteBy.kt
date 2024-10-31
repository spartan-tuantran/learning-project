package com.alext.codegen.annotations

/**
 * Each property annotated with [DeleteBy] will generate an extension function:
 *
 * delete by given column value, return single if column is unique, list if not
 *
 * ```
 *     public fun DatabaseContext.deleteByFooColumn(fooParam: Any): FooEntity? =
 *       transaction(primary) {
 *     FooTable.update(
 *        where = { FooTable.fooColumn eq fooParam },
 *        body = { update ->
 *          update[FooTable.deletedAt] = OffsetDateTime.now()
 *        }
 *      )
 *
 *     FooTable.select { FooTable.fooColumn eq fooParam }
 *     .andWhere { FooTable.deletedAt.isNotNull() }
 *     .singleOrNull()
 *     ?.let { convert(it) }
 *
 *   }
 * ```
 *
 * @param unique Indicate that the column is unique or not
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DeleteBy(
  val unique: Boolean = true
)
