package com.alext.redis.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KeyspaceCombinerTest {

  enum class Parent : Keyspace<Parent> {
    ONE
  }

  enum class Child : Keyspace<Child> {
    TWO
  }

  private val combiner = KeyCombiner.from(Parent.ONE, Child.TWO)

  @Test
  fun `multiple key should join together with double colons`() {
    assertThat(combiner.stamp("c", 1)).isEqualTo("one::two::c:1")
  }
}
