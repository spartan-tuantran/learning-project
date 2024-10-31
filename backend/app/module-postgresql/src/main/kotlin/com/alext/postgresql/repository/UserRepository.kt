package com.alext.postgresql.repository

import com.alext.database.runtime.DatabaseContext
import com.alext.postgresql.entity.UserEntity
import com.alext.postgresql.entity.UserEntityRepositoryExtension
import java.time.OffsetDateTime
import java.util.UUID

interface UserRepository {

  fun insert(entity: UserEntity): UserEntity

  fun byUsername(username: String): UserEntity?

  fun byUserId(id: UUID): UserEntity?

  fun updateLastLogin(userId: UUID, lastLoginAt: OffsetDateTime?): UserEntity?
}

class DefaultUserRepository(
  private val db: DatabaseContext
) : UserRepository, UserEntityRepositoryExtension {

  override fun insert(entity: UserEntity): UserEntity {
    return db.insert(entity)
  }

  override fun byUsername(
    username: String
  ): UserEntity? {
    return db.byUsername(username)
  }

  override fun byUserId(id: UUID): UserEntity? {
    return db.byId(id)
  }

  override fun updateLastLogin(userId: UUID, lastLoginAt: OffsetDateTime?): UserEntity? {
    return db.update(
      id = userId,
      lastLoginAt = lastLoginAt
    )
  }
}
