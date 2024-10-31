package com.alext.plugins.flyway.tasks.verify

import com.alext.plugins.flyway.models.SqlMigration
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor

object SqlOrderTest {

  private const val UNSET_MIGRATION_MARKER = "REORDER"

  /**
   * Verify all sql migrations from a given directory tree
   *
   * @param tree The directory which contains all migration files
   */
  operator fun invoke(tree: ConfigurableFileTree) {
    val migrations = collect(tree)
    verify(migrations.toTypedArray())
  }

  /**
   * Walk through a file directory and collect all migrations files.
   * This method assume all files must be in the follow format
   * ```
   *   [order]-[file-name].sql
   * ```
   * where `order` is the number
   *
   * Note: it will not attempt to verify the file name format.
   *
   * @param tree The file tree that contains all SQL migration files
   */
  private fun collect(tree: ConfigurableFileTree): List<SqlMigration> {
    val migrations = mutableListOf<SqlMigration>()
    tree.visit(object : FileVisitor {
      override fun visitFile(fileDetails: FileVisitDetails) {
        val file = fileDetails.file
        if (file.isFile) {
          val name = file.name
          if (name[0].isDigit() && !name.contains(UNSET_MIGRATION_MARKER)) {
            migrations.add(SqlMigration.from(file))
          }
        }
      }

      override fun visitDir(dirDetails: FileVisitDetails) {
        // Ignore directory
      }
    })
    return migrations
  }

  /**
   * Rule for all sql files:
   * - All SQL migrations file are from [0, n]
   * - There is no order duplication
   * Out of order or unsorted order is totally valid
   */
  private fun verify(migrations: Array<SqlMigration>) {
    val size = migrations.size
    if (size == 0) {
      throw GradleException("No SQL migrations found!")
    }

    migrations.forEach {
      if (it.order >= size) {
        throw GradleException("Invalid SQL migration: ${it.fileName}")
      }
    }

    val travelledSqlMigrationOrder = mutableSetOf<Int>()
    val duplicateOrders = mutableSetOf<SqlMigration>()

    for (migration in migrations) {
      if (travelledSqlMigrationOrder.contains(migration.order)) {
        duplicateOrders.add(migration)
      }
      travelledSqlMigrationOrder.add(migration.order)
    }

    if (duplicateOrders.size > 0) {
      throw GradleException("Duplicate SQL migrations found: ${duplicateOrders.map { it.fileName }}")
    }

    println("SQL migration looks great!")
  }
}
