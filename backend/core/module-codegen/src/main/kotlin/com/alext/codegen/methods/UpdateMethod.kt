package com.alext.codegen.methods

import com.alext.codegen.KspContext
import com.alext.codegen.annotations.Repository
import com.alext.codegen.symbol.ClassDeclaration
import com.alext.codegen.symbol.isListClass
import com.alext.database.runtime.DatabaseContext
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asTypeName
import java.util.UUID

/**
 * Generate an insert method using Kotlin Exposed syntax.
 */
class UpdateMethod(
  excludedProperties: Set<String>
) : RepositoryMethod {

  companion object {
    private val defaultExcludedProperties = setOf(
      "id",
      "createdAt",
      "updatedAt",
      "deletedAt"
    )
  }

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.update",
    "java.time.OffsetDateTime"
  )

  private val excludedProperties = defaultExcludedProperties + excludedProperties

  @OptIn(KspExperimental::class)
  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val typeName = declaration.typeName
    val tableQualifiedName = declaration.declaration.getAnnotationsByType(Repository::class).first().table
    val tableTypeName = ClassName.bestGuess(tableQualifiedName)
    val properties = declaration.properties.filter { property -> property.name !in excludedProperties }
    val tableName = tableTypeName.simpleName
    val updateStatement = properties
      .map { property ->
        when {
          property.enum -> {
            "${property.name}?.let { update[$tableName.${property.name}]·=·it.toString() }"
          }

          property.isListClass() -> {
            "${property.name}?.let { update[$tableName.${property.name}]·=·it.toTypedArray() }"
          }

          else -> {
            "${property.name}?.let { update[$tableName.${property.name}]·=·it }"
          }
        }
      }
      .plus("update[updatedAt] = OffsetDateTime.now()")
      .joinToString("\n")

    return listOf(
      FunSpec
        .builder("update")
        .receiver(DatabaseContext::class)
        .returns(typeName.copy(nullable = true))
        .apply {
          addParameter(
            ParameterSpec
              .builder("id", UUID::class.asTypeName())
              .build()
          )
          properties.forEach { property ->
            addParameter(
              ParameterSpec
                .builder(property.name, property.typeName.copy(nullable = true))
                .defaultValue("null")
                .build()
            )
          }
        }
        .addStatement(
          """
          |return transaction(primary) {
          | %T.update({ %T.id eq id }) { update ->
          |   %L
          | }
          |
          | %T.select { %T.id eq id }
          |   .singleOrNull()
          |   ?.let { convert(it) }
          |}
          """.trimMargin("|"),
          tableTypeName,
          tableTypeName,
          updateStatement,
          tableTypeName,
          tableTypeName
        )
        .build()
    )
  }
}
