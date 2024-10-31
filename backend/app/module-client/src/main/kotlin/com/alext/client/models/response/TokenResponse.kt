package com.alext.client.models.response

data class TokenResponse(
  val accessToken: String,
  val refreshToken: String,
  val tokenType: String,
  val expiresIn: Int
)
