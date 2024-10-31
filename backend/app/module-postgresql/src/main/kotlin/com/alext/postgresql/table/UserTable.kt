package com.alext.postgresql.table

import com.alext.database.exposed.extension.offsetDateTime
import com.alext.postgresql.constants.UserRole
import java.time.OffsetDateTime
import org.jetbrains.exposed.dao.id.UUIDTable

object UserTable : UUIDTable("user") {
  val username = text("username").uniqueIndex()
  val password = text("password")
  val role = text("role").default(UserRole.USER.name)
  val lastLoginAt = offsetDateTime("last_login_at").nullable()
  val createdAt = offsetDateTime("created_at").default(OffsetDateTime.now())
  val updatedAt = offsetDateTime("updated_at").nullable()
  val deletedAt = offsetDateTime("deleted_at").nullable()
}
