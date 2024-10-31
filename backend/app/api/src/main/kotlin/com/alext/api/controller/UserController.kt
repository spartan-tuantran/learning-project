package com.alext.api.controller

import com.alext.api.auth.RequiredRoles
import com.alext.api.auth.userId
import com.alext.api.manager.UserManager
import com.alext.client.models.User
import com.alext.client.models.request.UserCreateRequest
import com.alext.postgresql.constants.UserRole
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid

@Tag(name = "USER")
@Controller("/user")
@ExecuteOn(TaskExecutors.IO)
@Validated
class UserController (
  private val userManager: UserManager
) {

  @Post("/register")
  @Secured(SecurityRule.IS_ANONYMOUS)
  fun register(
    @Body
    @Valid
    request: UserCreateRequest
  ): User {
    val userEntity = userManager.addUser(request)
    return User.from(userEntity)
  }

  @Post("/register-admin")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @RequiredRoles(roles = [UserRole.USER])
  fun createAdminUser(
    @Body
    @Valid
    request: UserCreateRequest
  ): User {
    val userEntity = userManager.addUserWithRole(request, UserRole.ADMIN)
    return User.from(userEntity)
  }

  @Get("/me")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  fun me(
    authentication: Authentication
  ): User {
    val userEntity = userManager.byUserId(authentication.userId)
    return User.from(userEntity)
  }
}
