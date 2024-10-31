package com.alext.database.exposed.extension

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Index
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

class UpsertStatement<T : Any>(
  table: Table,
  conflictColumns: List<Column<*>>? = null,
  conflictColumn: Column<*>? = null,
  conflictIndex: Index? = null,
  where: Op<Boolean>? = null
) : InsertStatement<T>(table, false) {

  private val indexName: String
  private val indexColumns: List<Column<*>>
  private val index: Boolean
  private val idColumns = table.primaryKey?.columns ?: emptyArray()
  private val where: Op<Boolean>?

  init {
    when {
      conflictIndex != null -> {
        index = true
        indexName = conflictIndex.indexName
        indexColumns = conflictIndex.columns + idColumns
      }

      conflictColumn != null -> {
        index = false
        indexName = conflictColumn.name
        indexColumns = listOf(conflictColumn) + idColumns
      }

      conflictColumns != null -> {
        index = false
        indexName = conflictColumns.joinToString(",") { it.name }
        indexColumns = conflictColumns + idColumns
      }

      else -> {
        throw IllegalArgumentException("Either conflictIndex or conflictColumn must not be null")
      }
    }
    this.where = where
  }

  override fun prepareSQL(transaction: Transaction) = buildString {
    append(super.prepareSQL(transaction))

    val dialect = transaction.db.vendor
    if (dialect == "postgresql") {
      if (index) {
        append(" ON CONFLICT ON CONSTRAINT ")
        append(indexName)
      } else {
        append(" ON CONFLICT(")
        append(indexName)
        append(")")
      }
      where?.let {
        append(" WHERE $it")
      }
      append(" DO UPDATE SET ")
      values.keys
        .filter { it !in indexColumns }
        .joinTo(this) {
          if (it == table.columns.first { col -> col.name == "updated_at" }) {
            "${transaction.identity(it)}=NOW()"
          } else {
            "${transaction.identity(it)}=EXCLUDED.${transaction.identity(it)}"
          }
        }
    } else {
      append(" ON DUPLICATE KEY UPDATE ")
      values.keys
        .filter { it !in indexColumns }
        .joinTo(this) {
          if (it == table.columns.first { col -> col.name == "updated_at" }) {
            "${transaction.identity(it)}=NOW()"
          } else {
            "${transaction.identity(it)}=VALUES(${transaction.identity(it)})"
          }
        }
    }
  }
}

inline fun <T : Table> T.upsert(
  conflictColumns: List<Column<*>>? = null,
  conflictColumn: Column<*>? = null,
  conflictIndex: Index? = null,
  where: Op<Boolean>? = null,
  body: T.(UpsertStatement<Number>) -> Unit
): UpsertStatement<Number> {
  return UpsertStatement<Number>(this, conflictColumns, conflictColumn, conflictIndex, where).apply {
    body(this)
    execute(TransactionManager.current())
  }
}
