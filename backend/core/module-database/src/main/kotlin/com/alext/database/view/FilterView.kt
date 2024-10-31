package com.alext.database.view

data class FilterView<E>(
  val count: Long,
  val data: List<E>
)
