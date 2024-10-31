package com.alext.csv

import java.io.InputStream

interface CsvBuilder {
  /**
   * Set headers
   */
  fun headers(vararg headers: String): CsvBuilder

  /**
   * Bind rows
   */
  fun rows(vararg row: Array<Any?>): CsvBuilder

  /**
   * Bind a list of rows
   */
  fun rows(rows: List<Array<Any?>>): CsvBuilder

  /**
   * Build and return an [InputStream]
   */
  fun build(): InputStream

  companion object {

    fun default(): CsvBuilder {
      return DefaultCsvBuilder()
    }
  }
}
