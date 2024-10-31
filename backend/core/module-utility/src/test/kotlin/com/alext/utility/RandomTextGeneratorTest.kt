package com.alext.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RandomTextGeneratorTest {

  @Test
  fun `test generate`() {
    val set = HashSet<String>()
    repeat(1000) {
      set.add(RandomTextGenerator.generate(10))
    }
    assertThat(set.size).isEqualTo(1000)
    assertThat(set.random().length).isEqualTo(10)
  }
}
