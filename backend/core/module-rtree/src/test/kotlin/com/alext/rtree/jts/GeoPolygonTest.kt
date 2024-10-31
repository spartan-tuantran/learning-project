package com.alext.rtree.jts

import com.alext.rtree.misc.jts.toJtsGeometry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Polygon
import test.AbstractRTreeTest
import test.searcher.BruteforceSearcher
import test.searcher.RTreeSearcher

class GeoPolygonTest : AbstractRTreeTest() {

  private val regions = REGIONS
  private val bruteforce = BruteforceSearcher(regions)
  private val rtree = RTreeSearcher(regions)

  @Test
  fun `rtree vs bruteforce correctness - polygon`() {
    regions.forEach { (_, zone) ->
      val polygon = zone.region.toJtsGeometry<Polygon>()
      val r = rtree.search(polygon).map { it.id }.toSortedSet()
      val b = bruteforce.search(polygon).map { it.id }.toSortedSet()
      assertThat(r).isEqualTo(b)
    }
  }

  @Test
  fun `rtree vs bruteforce correctness - point`() {
    regions.forEach { (_, zone) ->
      val centroid = zone.region.toJtsGeometry<Polygon>().centroid
      val r = rtree.search(centroid).map { it.id }.toSortedSet()
      val b = bruteforce.search(centroid).map { it.id }.toSortedSet()
      assertThat(r).isEqualTo(b)
    }
  }
}
