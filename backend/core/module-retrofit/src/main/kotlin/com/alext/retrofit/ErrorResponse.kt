package com.alext.retrofit

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
  val message: String? = null,
  val code: String
)
