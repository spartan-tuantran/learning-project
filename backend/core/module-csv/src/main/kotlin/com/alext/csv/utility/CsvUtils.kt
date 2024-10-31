package com.alext.csv.utility

import java.io.InputStream
import java.io.InputStreamReader
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser

object CsvUtils {

  fun csvParser(inputStream: InputStream): CSVParser = CSVFormat.Builder.create(CSVFormat.DEFAULT)
    .apply {
      setIgnoreSurroundingSpaces(true)
      setHeader()
    }
    .build()
    .parse(InputStreamReader(inputStream))

  fun isCsvFile(fileName: String): Boolean {
    return fileName.endsWith(".csv", ignoreCase = true)
  }
}
