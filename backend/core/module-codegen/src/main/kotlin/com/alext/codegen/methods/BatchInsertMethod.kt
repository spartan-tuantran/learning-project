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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName

/**
 * Generate a batch insert method using Kotlin Exposed syntax.
 */
class BatchInsertMethod(
  private val excludedProperties: Set<String>
) : RepositoryMethod {

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.insert",
    "org.jetbrains.exposed.sql.batchInsert"
  )

  @OptIn(KspExperimental::class)
  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val typeName = declaration.typeName
    val tableQualifiedName = declaration.declaration.getAnnotationsByType(Repository::class).first().table
    val tableTypeName = ClassName.bestGuess(tableQualifiedName)
    val simpleTableName = tableTypeName.simpleName
    val properties = declaration.properties.filter { property -> property.name !in excludedProperties }

    val insertStatement = properties.map { property ->
      val nullableMark = if (property.typeName.isNullable) "?" else ""
      when {
        property.enum -> {
          "this[$simpleTableName.${property.name}] = entity.${property.name}$nullableMark.toString()"
        }

        property.isListClass() -> {
          "this[$simpleTableName.${property.name}] = entity.${property.name}$nullableMark.toTypedArray()"
        }

        else -> {
          "this[$simpleTableName.${property.name}] = entity.${property.name}"
        }
      }
    }
      .joinToString("\n")
    val returnTypeName = List::class.asTypeName().parameterizedBy(typeName)
    return listOf(
      FunSpec
        .builder("batchInsert")
        .receiver(DatabaseContext::class)
        .returns(returnTypeName)
        .addParameter(
          ParameterSpec
            .builder("entities", returnTypeName)
            .build()
        )
        .addParameter(
          ParameterSpec
            .builder("ignore", Boolean::class)
            .defaultValue("false")
            .build()
        )
        .addStatement(
          """
          |transaction(primary) {
          | %T.batchInsert(entities, ignore) { entity ->
          | %L
          |}
          |}
          """.trimMargin("|"),
          tableTypeName,
          insertStatement
        )
        .addStatement("return entities")
        .build()
    )
  }
}
