package com.alext.csv

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultCsvBuilderTest {

  @Test
  fun `write a simple CSV file`() {
    val input = CsvBuilder
      .default()
      .headers("one", "two")
      .rows(arrayOf(1, 2))
      .rows(arrayOf(3, 4))
      .build()

    val lines = input.bufferedReader().readLines()
    assertThat(lines[0]).isEqualTo("one,two")
    assertThat(lines[1]).isEqualTo("1,2")
    assertThat(lines[2]).isEqualTo("3,4")
  }
}
