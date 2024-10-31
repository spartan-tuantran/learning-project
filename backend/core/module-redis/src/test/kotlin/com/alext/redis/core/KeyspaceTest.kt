package com.alext.redis.core

import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KeyspaceTest {

  enum class TestKeyspace : Keyspace<TestKeyspace> {
    SIMPLE,
    HELLO_KOTLIN
  }

  @Test
  fun `keyspace key must have correct separator`() {
    assertThat(TestKeyspace.SIMPLE.key).isEqualTo("simple::")
    assertThat(TestKeyspace.HELLO_KOTLIN.key).isEqualTo("hello_kotlin::")
  }

  @Test
  fun `keyspace stamp should join all keys using colon as separator`() {
    assertThat(TestKeyspace.SIMPLE.stamp("sub", 1)).isEqualTo("simple::sub:1")
  }

  @Test
  fun `keyspace stamp must use object to string`() {
    val id = UUID(0, 0)
    assertThat(TestKeyspace.SIMPLE.stamp(id)).isEqualTo("simple::$id")
  }
}
