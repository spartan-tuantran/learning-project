package com.alext.example

import com.alext.codegen.annotations.Repository
import com.alext.codegen.annotations.SelectBy
import com.alext.codegen.annotations.UpsertNotNull
import com.alext.database.dao.Entity
import com.alext.database.exposed.extension.offsetDateTime
import com.alext.database.exposed.extension.textArray
import java.time.OffsetDateTime
import java.util.UUID
import org.jetbrains.exposed.dao.id.UUIDTable

enum class Platform {
  ANDROID,
  IOS
}

object UserTable : UUIDTable("users") {
  val name = text("name")
  val age = integer("phone")
  val bio = text("bio").nullable()
  val platform = text("platform")
  val nonNullTextArray = textArray("non_null_text_array")
  val nullableTextArray = textArray("nullable_text_array").nullable()
  val updatedAt = offsetDateTime("updated_at").nullable()
  val deletedAt = offsetDateTime("deleted_at").nullable()
  val createdAt = offsetDateTime("created_at").nullable()
}

@Repository("com.alext.UserTable")
data class UserEntity(
  @SelectBy
  override val id: UUID,
  @SelectBy(unique = true, replica = true)
  @UpsertNotNull
  val name: String = "hello",
  @SelectBy(unique = false, replica = true)
  val age: Int = 100,
  val bio: String? = null,
  val platform: Platform,
  val nonNullTextArray: List<String>,
  val nullableTextArray: List<String>? = null,
  val updatedAt: OffsetDateTime? = null,
  val deletedAt: OffsetDateTime? = null,
  val createdAt: OffsetDateTime? = null
) : Entity
