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

/**
 * Generate an insert method using Kotlin Exposed syntax.
 */
class InsertMethod(
  private val excludedProperties: Set<String>
) : RepositoryMethod {

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.insert"
  )

  @OptIn(KspExperimental::class)
  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val typeName = declaration.typeName
    val tableQualifiedName = declaration.declaration.getAnnotationsByType(Repository::class).first().table
    val tableTypeName = ClassName.bestGuess(tableQualifiedName)
    val properties = declaration.properties.filter { property -> property.name !in excludedProperties }
    val insertStatement = properties.map { property ->
      val nullableMark = if (property.typeName.isNullable) "?" else ""
      when {
        property.enum -> {
          "it[${property.name}] = entity.${property.name}$nullableMark.toString()"
        }

        property.isListClass() -> {
          "it[${property.name}] = entity.${property.name}$nullableMark.toTypedArray()"
        }

        else -> {
          "it[${property.name}] = entity.${property.name}"
        }
      }
    }
      .joinToString("\n")
    return listOf(
      FunSpec
        .builder("insert")
        .receiver(DatabaseContext::class)
        .returns(typeName)
        .addParameter(
          ParameterSpec
            .builder("entity", typeName)
            .build()
        )
        .addStatement(
          """
          |transaction(primary) {
          | %T.insert {
          |   %L
          | }
          |}
          """.trimMargin("|"),
          tableTypeName,
          insertStatement
        )
        .addStatement("return entity")
        .build()
    )
  }
}
