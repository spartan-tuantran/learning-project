package test

import com.alext.jackson.configured
import com.alext.rtree.extension.postgis.PostgisPolygon
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStreamReader
import java.util.UUID
import java.util.zip.GZIPInputStream

abstract class AbstractRTreeTest : ConsoleLogging, RectangleFactory {

  companion object {
    val JACKSON = ObjectMapper().configured()

    val REGIONS by lazy {
      lines("/regions.txt.gz").mapIndexed { index, line ->
        val comma = line.indexOf(",")
        val id = UUID.fromString(line.substring(0, comma))
        val wkt = line.substring(comma + 1)
        Region(
          id = id,
          name = index.toString(),
          region = PostgisPolygon(wkt)
        )
      }
        .associateBy { it.id }
    }

    fun lines(fileName: String): List<String> {
      val reader = InputStreamReader(GZIPInputStream(this::class.java.getResourceAsStream(fileName)))
      return reader.use {
        it.buffered().lines().toList()
      }
    }
  }
}
