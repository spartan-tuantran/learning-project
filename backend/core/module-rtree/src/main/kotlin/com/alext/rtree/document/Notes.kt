package com.alext.rtree.document

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Notes(
  val value: String = ""
)
