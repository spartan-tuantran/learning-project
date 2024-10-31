package com.alext.rtree.core.selector

import com.alext.rtree.api.Entry
import com.alext.rtree.api.RTree
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.geometry.Rectangle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.RectangleFactory

class SelectorTest : RectangleFactory {

  private val context = RTree.builder<Int, Rectangle>()
    .minChildren(1)
    .maxChildren(4)
    .context()

  private val nodes = listOf(
    Leaf.create(entries(1, 0), context),
    Leaf.create(entries(2, 3), context),
    Leaf.create(entries(5), context),
    Leaf.create(entries(3, 2), context)
  )

  private fun entries(n: Int): List<Entry<Int, Rectangle>> {
    return listOf(Entry.create(n, r(n)))
  }

  private fun entries(n: Int, m: Int): List<Entry<Int, Rectangle>> {
    return listOf(Entry.create(n, r(n.toDouble(), m.toDouble())))
  }

  @Test
  fun `selector minimal area increase`() {
    val result = Selector.MINIMAL_AREA_INCREASE.select(r(3), nodes)
    assertThat(result).isEqualTo(nodes[1])
  }

  @Test
  fun `selector minimal overlap area`() {
    val result = Selector.MINIMAL_OVERLAP_AREA.select(r(3), nodes)
    assertThat(result).isEqualTo(nodes[1])
  }
}
