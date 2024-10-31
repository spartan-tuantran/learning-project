package com.alext.client.models

import com.alext.postgresql.constants.UserRole
import com.alext.postgresql.entity.UserEntity
import java.time.OffsetDateTime
import java.util.UUID

data class User(
  val id: UUID,
  val username: String,
  val role: UserRole,
  val lastLoginAt: OffsetDateTime?
) {

  companion object {

    fun from(user: UserEntity): User {
      return User(
        id = user.id,
        username = user.username,
        role = user.role,
        lastLoginAt = user.lastLoginAt
      )
    }
  }
}
