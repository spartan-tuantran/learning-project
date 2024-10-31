package com.alext.codegen.annotations

/**
 * Each property annotated with [SelectBy] will generate two extension functions:
 *
 * * Select single
 *
 * ```
 *   public fun DatabaseContext.byFooColumn(fooColumn: String): FooEntity? =
 *       transaction(primary) {
 *     FooTable.select { FooTable.fooColumn eq fooColumn }
 *     .andWhere { FooTable.deletedAt.isNull() }
 *     .singleOrNull()
 *     ?.let { convert(it) }
 *   }
 * ```
 *
 * * Select multiple
 *
 * ```
 *   public fun DatabaseContext.byTextNotNulls(fooParams: List<Any>): List<FooEntity>
 *       = transaction(primary) {
 *       FooTable.select { FooTable.fooColumn inList fooParams }
 *       .andWhere { FooTable.deletedAt.isNull() }
 *       .map { convert(it) }
 *   }
 * ```
 *
 * @param unique Indicate that the column is unique or not
 * @param replica If true, the query will use replica db, false primary
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SelectBy(
  val unique: Boolean = true,
  val replica: Boolean = false
)
