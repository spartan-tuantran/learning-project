package com.alext.rtree.misc

import java.util.Random
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test

class StackTest {

  @Test
  fun `stack size`() {
    val s = Stack<Int>()
    s.push(1)
    assertThat(s.size()).isEqualTo(1)
    s.pop()
    assertThat(s.size()).isEqualTo(0)
  }

  @Test
  fun `stack empty`() {
    val s = Stack<Int>()
    s.push(1)
    assertThat(s.empty).isEqualTo(false)
    s.pop()
    assertThat(s.empty).isEqualTo(true)
  }

  @Test
  fun `push pop FILO`() {
    val s = Stack<Int>()
    s.push(1)
    s.push(2)
    s.push(3)
    var value = 3
    while (s.empty) {
      assertThat(s.pop()).isEqualTo(value)
      --value
    }
  }

  @Test
  fun `stack iterator`() {
    val random = Random()
    val list = listOf(
      random.nextInt(10),
      random.nextInt(10),
      random.nextInt(10)
    )
    val s = Stack<Int>()
    list.forEach { s.push(it) }
    assertThat(s.toList()).isEqualTo(list.reversed())
  }

  @Test
  fun `pop empty`() {
    val s = Stack<Int>()
    val result = catchThrowable { s.pop() }
    assertThat(result).isInstanceOf(NoSuchElementException::class.java)
  }

  @Test
  fun `peek empty`() {
    val s = Stack<Int>()
    val result = catchThrowable { s.peek() }
    assertThat(result).isInstanceOf(NoSuchElementException::class.java)
  }
}
