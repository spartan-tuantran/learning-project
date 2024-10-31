package com.alext.rtree.core

import com.alext.rtree.extension.jts.JtsPoint
import com.alext.rtree.extension.postgis.PostgisPoint
import com.alext.rtree.misc.jts.toJtsGeometry
import org.junit.jupiter.api.Test
import test.AbstractRTreeTest
import test.MemoryUsage
import test.Place
import test.searcher.RTreeSearcher

class RTreeZoneTest : AbstractRTreeTest() {

  @Test
  fun `load all areas into r-tree and search`() {
    val begin = System.currentTimeMillis()
    val finder = RTreeSearcher(REGIONS)
    val end = System.currentTimeMillis()
    purple {
      "load into cache took: ${end - begin} (ms)"
    }
    val n = 1
    repeat(n) {
      Place.values().forEach { place ->
        val lat = place.lat
        val lng = place.lng
        val point = PostgisPoint(lng, lat).toJtsGeometry<JtsPoint>()
        val b = System.nanoTime()
        val r = finder.search(point)
        val e = System.nanoTime()
        green {
          "Search took: ${e - b} (ns), result=${r.size}"
        }
        green {
          "Search took: ${(e - b) / 1_000_000.0} (ms), result=${r.size}\n"
        }
      }
      red {
        "memory usage: ${MemoryUsage.runtime()}"
      }
    }
  }
}
