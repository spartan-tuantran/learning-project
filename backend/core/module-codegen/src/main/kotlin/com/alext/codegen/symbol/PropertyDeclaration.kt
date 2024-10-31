package com.alext.codegen.symbol

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

data class PropertyDeclaration(
  val name: String,
  val typeName: TypeName,
  val annotations: List<KSAnnotation>,
  val type: KSType,
  val enum: Boolean
)

private val lazyListReflection by lazy {
  List::class
}

@OptIn(KotlinPoetKspPreview::class)
internal fun <T : Any> PropertyDeclaration.isSameClassName(
  annotationKClass: KClass<T>
): Boolean {
  return type.toClassName() == annotationKClass.asClassName()
}

@OptIn(KotlinPoetKspPreview::class)
internal fun PropertyDeclaration.isListClass(): Boolean {
  return type.toClassName() == lazyListReflection.asClassName()
}

internal fun <T : Annotation> PropertyDeclaration.isAnnotationPresent(
  annotationKClass: KClass<T>
): Boolean {
  return annotations.any { it.shortName.getShortName() == annotationKClass.simpleName }
}
