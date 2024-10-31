package com.alext.database.exposed

import com.alext.database.exposed.extension.columnOf
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import org.junit.jupiter.api.Test

class TableExtensionTest {

  object UserTable : UUIDTable("user") {
    val email: Column<String> = text("email").uniqueIndex()
    val imageUrl: Column<String?> = text("image_url").nullable()
    val createdAt: Column<Instant> = timestamp("created_at").default(Instant.now())
  }

  @Test
  fun `column() must return correct column`() {
    assertThat(UserTable.columnOf("email", UserTable.createdAt)).isEqualTo(UserTable.email)
    assertThat(UserTable.columnOf("imageUrl", UserTable.createdAt)).isEqualTo(UserTable.imageUrl)
    assertThat(UserTable.columnOf("createdAt", UserTable.createdAt)).isEqualTo(UserTable.createdAt)
    assertThat(UserTable.columnOf("hello", UserTable.createdAt)).isEqualTo(UserTable.createdAt)
  }
}
