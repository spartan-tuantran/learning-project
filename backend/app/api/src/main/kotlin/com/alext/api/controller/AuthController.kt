package com.alext.api.controller

import com.alext.api.manager.AuthManager
import com.alext.client.models.request.LoginRequest
import com.alext.client.models.request.RefreshTokenRequest
import com.alext.client.models.response.TokenResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid

@Tag(name = "LOGIN")
@Controller("/auth")
@ExecuteOn(TaskExecutors.IO)
@Validated
class AuthController(
  private val authManager: AuthManager
) {

  @Post("/login")
  @Secured(SecurityRule.IS_ANONYMOUS)
  fun login(
    @Body
    @Valid
    loginRequest: LoginRequest
  ): TokenResponse {
    return authManager.login(
      loginRequest.username,
      loginRequest.password
    )
  }

  @Post("/refresh-token")
  @Secured(SecurityRule.IS_ANONYMOUS)
  fun refreshToken(
    @Body
    @Valid
    refreshTokenRequest: RefreshTokenRequest
  ) : TokenResponse {
    return authManager.refreshToken(refreshTokenRequest.token)
  }
}
