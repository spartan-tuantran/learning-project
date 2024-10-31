package com.alext.codegen.annotations

/**
 * Annotation for upsert action,
 * Fields are marked with this annotation will be gen as non-null for upsert action
 *
 * Because we don't know the action will be updating or inserting. So for safety, we marked them
 * non-null and required to put value rather than null
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class UpsertNotNull
