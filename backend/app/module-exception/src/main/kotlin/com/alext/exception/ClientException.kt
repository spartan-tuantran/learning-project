package com.alext.exception

data class ClientException(
  val httpStatusCode: Int,
  val code: String,
  override val message: String = ""
) : RuntimeException(message)
