package com.alext.codegen.methods

import com.alext.codegen.KspContext
import com.alext.codegen.annotations.Repository
import com.alext.codegen.symbol.ClassDeclaration
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import java.util.Locale

internal interface RepositoryMethod {
  /**
   * A list of fully qualified name to be imported
   */
  val imports: List<String>

  /**
   * Build and return a list of [FunSpec]s from a class declaration
   */
  fun funSpec(declaration: ClassDeclaration, context: KspContext): List<FunSpec>

  @OptIn(KspExperimental::class)
  val ClassDeclaration.tableClassName: ClassName
    get() {
      val tableQualifiedName = declaration.getAnnotationsByType(Repository::class).first().table
      return ClassName.bestGuess(tableQualifiedName)
    }
}

internal fun String.capitalized(): String {
  return replaceFirstChar {
    if (it.isLowerCase()) {
      it.titlecase(Locale.getDefault())
    } else {
      it.toString()
    }
  }
}
