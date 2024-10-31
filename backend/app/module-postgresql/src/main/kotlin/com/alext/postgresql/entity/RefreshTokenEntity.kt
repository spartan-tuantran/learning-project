package com.alext.postgresql.entity

import com.alext.codegen.annotations.Repository
import com.alext.codegen.annotations.SelectBy
import com.alext.database.dao.Entity
import java.time.OffsetDateTime
import java.util.UUID

@Repository(table = "com.alext.postgresql.table.RefreshTokenTable")
data class RefreshTokenEntity(
  override var id: UUID = UUID.randomUUID(),
  @SelectBy(unique = true, replica = false)
  val userId: UUID,
  @SelectBy(unique = true, replica = false)
  val token: String,
  val expiresIn: Long = 604_800,
  val createdAt: OffsetDateTime = OffsetDateTime.now(),
  val updatedAt: OffsetDateTime? = null,
  val deletedAt: OffsetDateTime? = null
) : Entity {

  fun isExpired(): Boolean {
    val timeInFuture = createdAt.plusSeconds(expiresIn)
    return timeInFuture.isBefore(OffsetDateTime.now())
  }
}
