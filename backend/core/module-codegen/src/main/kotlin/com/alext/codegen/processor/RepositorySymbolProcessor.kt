@file:OptIn(KspExperimental::class, KotlinPoetKspPreview::class, KotlinPoetKspPreview::class)

package com.alext.codegen.processor

import com.alext.codegen.KspContext
import com.alext.codegen.annotations.DeleteBy
import com.alext.codegen.annotations.Exclude
import com.alext.codegen.annotations.Repository
import com.alext.codegen.annotations.SelectBy
import com.alext.codegen.methods.BatchInsertMethod
import com.alext.codegen.methods.ConvertResultRowMethod
import com.alext.codegen.methods.ConvertResultSetMethod
import com.alext.codegen.methods.DeleteByIdMethod
import com.alext.codegen.methods.DeleteByMethod
import com.alext.codegen.methods.InsertMethod
import com.alext.codegen.methods.RepositoryMethod
import com.alext.codegen.methods.SelectByMethod
import com.alext.codegen.methods.SelectOneByIdMethod
import com.alext.codegen.methods.UpdateMethod
import com.alext.codegen.methods.UpsertMethod
import com.alext.codegen.symbol.ClassDeclaration
import com.alext.database.dao.Entity
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class RepositorySymbolProcessor(
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger
) : SymbolProcessor {

  companion object {
    private val UUID_TYPE_NAME = UUID::class.asTypeName()
    private val ENTITY_TYPE_NAME = Entity::class.asTypeName()
  }

  private val excludedProperties = CopyOnWriteArrayList<String>()

  override fun process(resolver: Resolver): List<KSAnnotated> {
    val symbols = resolver.getSymbolsWithAnnotation(Repository::class.qualifiedName!!)
    symbols
      .filter { it is KSClassDeclaration && it.validate() }
      .forEach { it.accept(Visitor(resolver), Unit) }
    return symbols.filterNot { it.validate() }.toList()
  }

  private inner class Visitor(
    private val resolver: Resolver
  ) : KSVisitorVoid() {

    private val propertyMethods = mutableListOf<RepositoryMethod>()

    @OptIn(KotlinPoetKspPreview::class)
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      val qualifiedName = classDeclaration.qualifiedName?.asString() ?: run {
        logger.error(
          "@Repository must target classes with qualified names",
          classDeclaration
        )
        return
      }

      if (!classDeclaration.isDataClass()) {
        logger.error(
          "@Repository cannot target non-data class $qualifiedName",
          classDeclaration
        )
        return
      }

      if (classDeclaration.typeParameters.any()) {
        logger.error(
          "@Repository must target data classes with no type parameters",
          classDeclaration
        )
        return
      }

      val declaration = ClassDeclaration(declaration = classDeclaration).apply {
        val noIdProperty = properties.none { property -> property.name == "id" && property.typeName == UUID_TYPE_NAME }
        if (noIdProperty) {
          logger.error(
            "@Repository must contain `id` property as UUID",
            classDeclaration
          )
          return
        }

        val noEntityInterface = declaration
          .superTypes
          .map { it.toTypeName(TypeParameterResolver.EMPTY) }
          .none { it == ENTITY_TYPE_NAME }

        if (noEntityInterface) {
          logger.error(
            "@Repository must implement interface ${Entity::class.simpleName}",
            classDeclaration
          )
          return
        }
      }

      classDeclaration.getAllProperties().forEach {
        it.accept(this, Unit)
      }

      val excludedPropertySet = excludedProperties.toSet()
      val methods = propertyMethods + listOf(
        SelectOneByIdMethod,
        DeleteByIdMethod,
        InsertMethod(excludedPropertySet),
        ConvertResultRowMethod(excludedPropertySet),
        ConvertResultSetMethod(excludedPropertySet),
        UpdateMethod(excludedPropertySet),
        UpsertMethod(excludedPropertySet),
        BatchInsertMethod(excludedPropertySet)
      )
      val context = KspContext.from(resolver, logger)
      val interfaceName = classDeclaration.simpleName.asString() + "RepositoryExtension"

      FileSpec
        .builder(
          packageName = declaration.packageName,
          fileName = interfaceName
        )
        .indent("  ")
        .addFileComment("!!!WARNING: File under the build folder are generated and should not be edited.")
        .apply {
          addType(
            TypeSpec
              .interfaceBuilder(interfaceName)
              .apply {
                methods.forEach { m ->
                  val imports = m.imports.toSortedSet()
                  imports.forEach { i ->
                    val lastDot = i.lastIndexOf(".")
                    addImport(i.substring(0, lastDot), i.substring(lastDot + 1))
                  }
                  m.funSpec(declaration, context).forEach { f ->
                    addFunction(f)
                  }
                }
                addType(
                  TypeSpec
                    .companionObjectBuilder()
                    .addSuperinterface(ClassName("", interfaceName))
                    .build()
                )
              }
              .build()
          )
        }
        .build()
        .writeTo(codeGenerator = codeGenerator, aggregating = false)
    }

    @OptIn(KspExperimental::class)
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
      if (property.isAnnotationPresent(SelectBy::class)) {
        propertyMethods += SelectByMethod(property)
      }
      if (property.isAnnotationPresent(DeleteBy::class)) {
        propertyMethods += DeleteByMethod(property)
      }
      if (property.isAnnotationPresent(Exclude::class)) {
        excludedProperties.add(property.simpleName.asString())
      }
    }
  }

  private fun KSClassDeclaration.isDataClass() =
    modifiers.contains(Modifier.DATA)
}
