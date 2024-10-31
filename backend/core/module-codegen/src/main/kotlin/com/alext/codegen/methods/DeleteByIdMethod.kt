package com.alext.codegen.methods

import com.alext.codegen.KspContext
import com.alext.codegen.symbol.ClassDeclaration
import com.alext.database.runtime.DatabaseContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asTypeName
import java.util.UUID

/**
 * Generate the method to delete an entity by id.
 */
object DeleteByIdMethod : RepositoryMethod {
  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.andWhere",
    "org.jetbrains.exposed.sql.update",
    "java.time.OffsetDateTime"
  )

  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val tableClassName = declaration.tableClassName

    return listOf(
      FunSpec
        .builder("deleteById")
        .receiver(DatabaseContext::class)
        .returns(declaration.typeName.copy(nullable = true))
        .addParameter(
          ParameterSpec
            .builder("id", UUID::class.asTypeName())
            .build()
        )
        .addStatement(
          """
          |return transaction(primary) {
          | %T.update({ %T.id eq id }) { update ->
          |   update[deletedAt] = OffsetDateTime.now()
          | }
          |
          | %T.select { %T.id eq id }
          |  .andWhere { %T.deletedAt.isNotNull() }
          |  .singleOrNull()
          |   ?.let { convert(it) }
          |}
          """.trimMargin("|"),
          tableClassName,
          tableClassName,
          tableClassName,
          tableClassName,
          tableClassName
        )
        .build()
    )
  }
}
