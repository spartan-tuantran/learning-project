package com.alext.codegen.methods

import com.alext.database.runtime.DatabaseContext
import com.alext.codegen.KspContext
import com.alext.codegen.annotations.SelectBy
import com.alext.codegen.symbol.ClassDeclaration
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * Generate a select by column method.
 */
class SelectByMethod(
  private val property: KSPropertyDeclaration
) : RepositoryMethod {

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.andWhere"
  )

  @OptIn(KspExperimental::class, KotlinPoetKspPreview::class)
  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val classTypeName = declaration.typeName
    val tableClassName = declaration.tableClassName
    val propertyTypeName = property.type.toTypeName(TypeParameterResolver.EMPTY)
    val name = property.simpleName.asString()
    val replica = property.getAnnotationsByType(SelectBy::class).firstOrNull()?.replica == true
    val unique = property.getAnnotationsByType(SelectBy::class).firstOrNull()?.unique == true
    val database = if (replica) "replica" else "primary"
    return listOfNotNull(
      if (name == "id") {
        null
      } else {
        FunSpec
          .builder(name.methodName())
          .receiver(DatabaseContext::class)
          .returns(
            if (unique) {
              classTypeName.copy(nullable = true)
            } else {
              List::class.asTypeName().parameterizedBy(classTypeName)
            }
          )
          .addParameter(
            ParameterSpec
              .builder(name, propertyTypeName.copy(nullable = false))
              .build()
          )
          .apply {
            if (unique) {
              addStatement(
                """
                |return transaction($database) {
                |  %T.select { %T.$name eq $name }
                |  .andWhere { %T.deletedAt.isNull() }
                |  .singleOrNull()
                |  ?.let { convert(it) }
                |}
                """.trimMargin(),
                tableClassName,
                tableClassName,
                tableClassName
              )
            } else {
              addStatement(
                """
                |return transaction($database) {
                |  %T.select { %T.$name eq $name }
                |  .andWhere { %T.deletedAt.isNull() }
                |  .map { convert(it) }
                |}
                """.trimMargin(),
                tableClassName,
                tableClassName,
                tableClassName
              )
            }
          }
          .build()
      },

      // Select list of item or empty by list of value
      FunSpec
        .builder("${name.methodName()}s")
        .receiver(DatabaseContext::class)
        .returns(List::class.asTypeName().parameterizedBy(classTypeName))
        .addParameter(
          ParameterSpec
            .builder(
              "${name}s",
              List::class.asTypeName().parameterizedBy(propertyTypeName.copy(nullable = false))
            )
            .build()
        )
        .apply {
          addStatement(
            """
            |return transaction($database) {
            |  %T.select { %T.$name inList ${name}s }
            |  .andWhere { %T.deletedAt.isNull() }
            |  .map { convert(it) }
            |}
            """.trimMargin(),
            tableClassName,
            tableClassName,
            tableClassName
          )
        }
        .build()
    )
  }

  private fun String.methodName(): String {
    return "by" + this.capitalized()
  }
}
