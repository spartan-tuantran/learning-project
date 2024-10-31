package com.alext.io

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ResourcesTest {

  @Test
  fun `stream() should return text content`() {
    Resources.stream("hello.txt")!!.bufferedReader().useLines {
      assertThat(it.toList()).containsExactly("one", "two")
    }
    Resources.stream("/hello.txt")!!.bufferedReader().useLines {
      assertThat(it.toList()).containsExactly("one", "two")
    }
    Resources.stream("/hello.txt", relative = true)!!.bufferedReader().useLines {
      assertThat(it.toList()).containsExactly("one", "two")
    }

    val data = Resources.stream("/hello.txt")!!.bufferedReader().readText()
    assertTrue(
      data == "one\ntwo\n" || data == "one\r\ntwo\r\n"
    )
  }

  @Test
  fun `file() should return text content`() {
    Resources.file("hello.txt")!!.bufferedReader().useLines {
      assertThat(it.toList()).containsExactly("one", "two")
    }
    Resources.file("/hello.txt")!!.bufferedReader().useLines {
      assertThat(it.toList()).containsExactly("one", "two")
    }
    Resources.file("/hello.txt", relative = true)!!.bufferedReader().useLines {
      assertThat(it.toList()).containsExactly("one", "two")
    }
  }
}
