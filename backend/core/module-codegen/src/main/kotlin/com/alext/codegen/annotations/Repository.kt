package com.alext.codegen.annotations

/**
 * Annotation for entity classes.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Repository(
  val table: String
)
