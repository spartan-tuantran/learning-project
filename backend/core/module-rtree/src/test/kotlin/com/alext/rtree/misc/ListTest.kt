package com.alext.rtree.misc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListTest {

  @Test
  fun `add an element`() {
    val list = listOf(1, 2)
    assertThat(list.add(3)).isEqualTo(listOf(1, 2, 3))
  }

  @Test
  fun `remove elements`() {
    val list = listOf(1, 2)
    assertThat(list.remove(list)).isEqualTo(listOf<Int>())
    assertThat(emptyList<Int>().remove(list)).isEmpty()
  }

  @Test
  fun `replace elements`() {
    val list = listOf(1, 2, 3)
    val result = list.replace(2, listOf(4, 5))
    assertThat(result).isEqualTo(listOf(1, 3, 4, 5))
  }
}
