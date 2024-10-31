package com.alext.api.auth

import com.alext.postgresql.constants.UserRole
import io.micronaut.security.authentication.Authentication
import java.util.UUID

val Authentication.userId: UUID
  get() {
    return UUID.fromString(attributes["userId"].toString())
  }

val Authentication.userRoles: List<UserRole>
  get() {
    return roles.map { UserRole.valueOf(it.uppercase()) }
  }
