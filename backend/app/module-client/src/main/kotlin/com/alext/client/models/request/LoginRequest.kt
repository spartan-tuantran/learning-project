package com.alext.client.models.request

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Introspected
data class LoginRequest(
  @field:NotBlank(message = "username is required")
  val username: String,

  @field:NotBlank(message = "password is required")
  @field:Size(min = 8, message = "password length should be at least 8 characters long")
  val password: String
)
