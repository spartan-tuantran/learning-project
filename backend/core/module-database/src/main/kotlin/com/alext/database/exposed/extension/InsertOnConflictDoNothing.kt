package com.alext.database.exposed.extension

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

class InsertOnConflictDoNothing(
  table: Table
) : InsertStatement<ResultRow>(table, false) {

  override fun prepareSQL(transaction: Transaction): String {
    return super.prepareSQL(transaction) + " ON CONFLICT DO NOTHING"
  }
}

fun <T : Table> T.insertOnConflictDoNothing(
  body: T.(InsertOnConflictDoNothing) -> Unit
): InsertStatement<ResultRow> {
  return InsertOnConflictDoNothing(this).apply {
    body(this)
    execute(TransactionManager.current())
  }
}
