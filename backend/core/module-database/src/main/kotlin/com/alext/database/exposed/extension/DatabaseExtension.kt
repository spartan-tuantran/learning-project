package com.alext.database.exposed.extension

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Create new transaction for database [Database]
 *
 * @param autoCommit Flag to indicate whether this transaction should be auto-commit
 */
fun Database.newTransaction(autoCommit: Boolean = false): Transaction? {
  val manager = TransactionManager.managerFor(this)
  return manager?.newTransaction()?.apply {
    connection.autoCommit = autoCommit
  }
}

/**
 * Execute a [block] using the receiver transaction and safely rollback if any exception is thrown
 *
 * @param block A block to be executed
 */
inline fun <T : Any> Transaction.execute(block: (Transaction) -> T): T {
  return try {
    val result = block(this)
    commit()
    result
  } catch (e: Exception) {
    rollback()
    throw e
  } finally {
    close()
  }
}

/**
 * Truncate all tables in the database except for the [excludedTables]
 *
 * @param excludedTables A set of table names to be excluded from truncation
 */
fun Database.truncateAllTables(
  excludedTables: Set<String> = setOf(
    "geography_columns",
    "geometry_columns",
    "spatial_ref_sys",
    "schema_version"
  )
): Int {
  return transaction(this) {
    val connection = TransactionManager.current().connection
    val statement = connection.prepareStatement(
      "SELECT table_name FROM information_schema.tables WHERE table_schema='public'",
      emptyArray()
    )
    val result = statement.executeQuery()
    val tables = mutableListOf<String>()
    result.use {
      while (result.next()) {
        val name = result.getString("table_name")
        if (name !in excludedTables) {
          tables += name
        }
      }
      tables.sumOf { table ->
        connection.prepareStatement("TRUNCATE TABLE $table", emptyArray()).executeUpdate()
      }
    }
  }
}
