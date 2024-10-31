package com.alext.rtree.core.splitter

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.HasGeometry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RStarSplitterTest {
  private val factory = Geometries
  private val splitter = com.alext.rtree.core.splitter.RStarSplitter()

  @Test
  fun `pairs of size 2`() {
    val list = listOf<HasGeometry>(
      factory.point(1.0, 1.0).mbr,
      factory.point(2.0, 2.0).mbr,
      factory.point(3.0, 3.0).mbr,
      factory.point(4.0, 4.0).mbr,
      factory.point(5.0, 5.0).mbr
    )
    val result = splitter.pairsOf(minSize = 2, list = list)
    assertThat(result).hasSize(2)
  }
}
