package com.alext.csv

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

internal class DefaultCsvBuilder : CsvBuilder {

  private val headers: MutableList<String> = mutableListOf()
  private val rows: MutableList<Array<Any?>> = mutableListOf()

  override fun headers(vararg headers: String): CsvBuilder {
    this.headers.addAll(headers)
    return this
  }

  override fun rows(vararg row: Array<Any?>): CsvBuilder {
    this.rows.addAll(row)
    return this
  }

  override fun rows(rows: List<Array<Any?>>): CsvBuilder {
    this.rows.addAll(rows)
    return this
  }

  override fun build(): InputStream {
    val header = CSVFormat.DEFAULT
      .builder()
      .setHeader(*headers.toTypedArray())
      .build()
    val out = ByteArrayOutputStream()
    val writer = OutputStreamWriter(out, StandardCharsets.UTF_8)
    val printer = CSVPrinter(writer, header)
    writer.use {
      printer.use { p ->
        rows.forEach { r ->
          p.printRecords(r)
        }
        p.flush()
      }
    }
    return ByteArrayInputStream(out.toByteArray())
  }
}
