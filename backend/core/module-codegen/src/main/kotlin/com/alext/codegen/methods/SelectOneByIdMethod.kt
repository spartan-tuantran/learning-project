package com.alext.codegen.methods

import com.alext.codegen.KspContext
import com.alext.codegen.symbol.ClassDeclaration
import com.alext.database.runtime.DatabaseContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asTypeName
import java.util.UUID

/**
 * Generate a select an entity by id.
 */
object SelectOneByIdMethod : RepositoryMethod {

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.select",
    "org.jetbrains.exposed.sql.and"
  )

  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val typeName = declaration.typeName
    val tableClassName = declaration.tableClassName
    val database = "replica"
    return listOf(
      FunSpec
        .builder("byId")
        .receiver(DatabaseContext::class)
        .returns(typeName.copy(nullable = true))
        .addParameter(
          ParameterSpec
            .builder("id", UUID::class.asTypeName())
            .build()
        )
        .addStatement(
          """
          |return transaction($database) {
          | %T.select { (%T.id eq id) and (%T.deletedAt.isNull()) }
          |   .singleOrNull()
          |   ?.let { convert(it) }
          |}
          """.trimMargin(),
          tableClassName,
          tableClassName,
          tableClassName
        )
        .build()
    )
  }
}
