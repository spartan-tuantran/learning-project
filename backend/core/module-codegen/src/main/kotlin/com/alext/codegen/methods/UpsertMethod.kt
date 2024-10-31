package com.alext.codegen.methods

import com.alext.codegen.KspContext
import com.alext.codegen.annotations.Repository
import com.alext.codegen.annotations.UpsertNotNull
import com.alext.codegen.symbol.ClassDeclaration
import com.alext.codegen.symbol.PropertyDeclaration
import com.alext.codegen.symbol.isAnnotationPresent
import com.alext.codegen.symbol.isListClass
import com.alext.database.runtime.DatabaseContext
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec

/**
 * Generate an upsert method using Kotlin Exposed syntax.
 */
class UpsertMethod(
  excludedProperties: Set<String>
) : RepositoryMethod {

  companion object {
    private val defaultExcludedProperties = setOf(
      "createdAt",
      "updatedAt",
      "deletedAt"
    )
  }

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.update",
    "java.time.OffsetDateTime",
    "com.alext.database.exposed.extension.upsert",
    "org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull"
  )

  private val excludedProperties = defaultExcludedProperties + excludedProperties

  @OptIn(KspExperimental::class)
  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val typeName = declaration.typeName
    val tableQualifiedName = declaration.declaration.getAnnotationsByType(Repository::class).first().table
    val tableTypeName = ClassName.bestGuess(tableQualifiedName)
    val properties = declaration.properties.filter { property -> property.name !in excludedProperties }
    val tableName = tableTypeName.simpleName

    val upsertStatement = properties
      .map { property ->
        val nullableMark = if (shouldMarkFieldNotNull(property)) "" else "?"
        when {
          property.enum -> {
            "${property.name}$nullableMark.let { update[$tableName.${property.name}]·=·it.toString() }"
          }

          property.isListClass() -> {
            "${property.name}$nullableMark.let { update[$tableName.${property.name}]·=·it.toTypedArray() }"
          }

          else -> {
            "${property.name}$nullableMark.let { update[$tableName.${property.name}]·=·it }"
          }
        }
      }
      .plus("update[updatedAt] = OffsetDateTime.now()")
      .joinToString("\n")

    return listOf(
      FunSpec
        .builder("upsert")
        .receiver(DatabaseContext::class)
        .returns(typeName.copy(nullable = true))
        .apply {
          properties.forEach { property ->
            val newTypeName = property.typeName.copy(nullable = !shouldMarkFieldNotNull(property))
            val parameterSpec = ParameterSpec.builder(property.name, newTypeName)
            if (newTypeName.isNullable) {
              parameterSpec.defaultValue("null")
            }
            addParameter(parameterSpec.build())
          }
        }
        .addStatement(
          """
          |return transaction(primary) {
          | %T.upsert(
          | conflictColumn = %T.id,
          | where = %T.deletedAt.isNull()
          | ) { update ->
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
          tableTypeName,
          upsertStatement,
          tableTypeName,
          tableTypeName
        )
        .build()
    )
  }

  private fun shouldMarkFieldNotNull(property: PropertyDeclaration): Boolean {
    val hasUpsertNotNull = property.isAnnotationPresent(UpsertNotNull::class)
    return hasUpsertNotNull || property.name == "id"
  }
}
