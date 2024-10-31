package com.alext.client.models.request

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank

@Introspected
data class RefreshTokenRequest(
  @field:NotBlank
  val token: String
)
