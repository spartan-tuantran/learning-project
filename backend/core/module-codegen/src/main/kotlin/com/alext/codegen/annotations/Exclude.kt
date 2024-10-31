package com.alext.codegen.annotations

/**
 * Each property annotated with [Exclude] will be dropped from code generation.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Exclude
