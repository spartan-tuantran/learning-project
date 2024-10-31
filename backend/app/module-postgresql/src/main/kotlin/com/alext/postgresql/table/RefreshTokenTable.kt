package com.alext.postgresql.table

import com.alext.database.exposed.extension.offsetDateTime
import java.time.OffsetDateTime
import org.jetbrains.exposed.dao.id.UUIDTable

object RefreshTokenTable : UUIDTable("refresh_token") {
  val userId = uuid("user_id")
  val token = text("token").uniqueIndex()
  val expiresIn = long("expires_in").default(604800)
  val createdAt = offsetDateTime("created_at").default(OffsetDateTime.now())
  val updatedAt = offsetDateTime("updated_at").nullable()
  val deletedAt = offsetDateTime("deleted_at").nullable()
}
