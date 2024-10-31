package com.alext.postgresql.repository

import com.alext.database.runtime.DatabaseContext
import com.alext.postgresql.entity.RefreshTokenEntity
import com.alext.postgresql.entity.RefreshTokenEntityRepositoryExtension
import com.alext.postgresql.table.RefreshTokenTable
import java.time.OffsetDateTime
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

interface RefreshTokenRepository {

  fun insert(entity: RefreshTokenEntity): RefreshTokenEntity

  fun byToken(token: String): RefreshTokenEntity?

  fun deleteByUserId(userId: UUID)

  fun hardDeleteByUserId(userId: UUID)

  fun deleteById(id: UUID)

  fun hardDeleteById(id: UUID)
}

class DefaultRefreshTokenRepository(
  private val db: DatabaseContext
) : RefreshTokenRepository, RefreshTokenEntityRepositoryExtension {

  override fun deleteByUserId(userId: UUID) {
    return transaction(db.primary) {
      RefreshTokenTable.update({ RefreshTokenTable.userId eq userId }) { update ->
        update[deletedAt] = OffsetDateTime.now()
      }
    }
  }

  override fun hardDeleteByUserId(userId: UUID) {
    return transaction(db.primary) {
      RefreshTokenTable.deleteWhere { RefreshTokenTable.userId eq userId }
    }
  }

  override fun deleteById(id: UUID) {
    db.deleteById(id)
  }

  override fun hardDeleteById(id: UUID) {
    return transaction(db.primary) {
      RefreshTokenTable.deleteWhere { userId eq id }
    }
  }

  override fun insert(entity: RefreshTokenEntity): RefreshTokenEntity {
    return db.insert(entity)
  }

  override fun byToken(token: String): RefreshTokenEntity? {
    return db.byToken(token)
  }
}
