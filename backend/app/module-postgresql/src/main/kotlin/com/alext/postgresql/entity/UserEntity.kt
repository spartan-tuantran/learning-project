package com.alext.postgresql.entity

import com.alext.codegen.annotations.Repository
import com.alext.codegen.annotations.SelectBy
import com.alext.database.dao.Entity
import com.alext.postgresql.constants.UserRole
import java.time.OffsetDateTime
import java.util.UUID

@Repository(table = "com.alext.postgresql.table.UserTable")
data class UserEntity(
  override val id: UUID = UUID.randomUUID(),
  @SelectBy(unique = true, replica = false)
  val username: String,
  val password: String,
  val role: UserRole = UserRole.USER,
  val lastLoginAt: OffsetDateTime? = null,
  val createdAt: OffsetDateTime = OffsetDateTime.now(),
  val updatedAt: OffsetDateTime? = null,
  val deletedAt: OffsetDateTime? = null
) : Entity
