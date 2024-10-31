package com.alext.rtree.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Point
import test.AbstractRTreeTest
import test.randomPoints
import test.searcher.BruteforceSearcher
import test.searcher.RTreeSearcher

class RTreePointTest : AbstractRTreeTest() {
  // Load all zones from file
  private val zones = REGIONS

  // Insert into naive tree
  private val bruteforce = BruteforceSearcher(zones)
  private val rtree = RTreeSearcher(zones)

  @Test
  fun `insert zones from files then race turtle-hare`() {
    randomPoints(100) { point: Point ->
      // Search from tree
      val b = bruteforce.search(point)
      val r = rtree.search(point)
      // Data must be the same
      assertThat(b.map { it.id }.toSortedSet()).isEqualTo(r.map { it.id }.toSortedSet())
    }
  }
}
