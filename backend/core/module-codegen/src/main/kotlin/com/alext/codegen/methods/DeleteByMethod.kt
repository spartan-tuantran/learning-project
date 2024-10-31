package com.alext.codegen.methods

import com.alext.codegen.KspContext
import com.alext.codegen.annotations.DeleteBy
import com.alext.codegen.symbol.ClassDeclaration
import com.alext.database.runtime.DatabaseContext
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
 * Generate a delete by column method.
 */
class DeleteByMethod(
  private val property: KSPropertyDeclaration
) : RepositoryMethod {

  override val imports: List<String> = listOf(
    "org.jetbrains.exposed.sql.transactions.transaction",
    "org.jetbrains.exposed.sql.andWhere",
    "org.jetbrains.exposed.sql.update",
    "java.time.OffsetDateTime"
  )

  @OptIn(KspExperimental::class, KotlinPoetKspPreview::class)
  override fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec> {
    val classTypeName = declaration.typeName
    val tableClassName = declaration.tableClassName
    val propertyTypeName = property.type.toTypeName(TypeParameterResolver.EMPTY)
    val name = property.simpleName.asString()
    val unique = property.getAnnotationsByType(DeleteBy::class).firstOrNull()?.unique == true
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
                |return transaction(primary) {
                |  %T.update(
                |     where = { %T.$name eq $name },
                |     body = { update -> 
                |       update[%T.deletedAt] = OffsetDateTime.now()
                |     }
                |   )             
                |
                |  %T.select { %T.$name eq $name }
                |  .andWhere { %T.deletedAt.isNotNull() }
                |  .singleOrNull()
                |  ?.let { convert(it) }               
                |  
                |}
                """.trimMargin(),
                tableClassName,
                tableClassName,
                tableClassName,
                tableClassName,
                tableClassName,
                tableClassName
              )
            } else {
              addStatement(
                """
                | return transaction(primary) {
                |  %T.update(
                |     where = { %T.$name eq $name },
                |     body = { update -> 
                |       update[%T.deletedAt] = OffsetDateTime.now()
                |     }
                |   )             
                |
                |  %T.select { %T.$name eq $name }
                |  .andWhere { %T.deletedAt.isNotNull() }
                |  .map { convert(it) }                       
                |  
                |}
                """.trimMargin(),
                tableClassName,
                tableClassName,
                tableClassName,
                tableClassName,
                tableClassName,
                tableClassName
              )
            }
          }
          .build()
      }
    )
  }

  private fun String.methodName(): String {
    return "deleteBy" + this.capitalized()
  }
}
