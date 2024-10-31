package com.alext.rtree.core

import com.alext.rtree.Item
import com.alext.rtree.api.Entry
import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Point
import com.alext.rtree.core.geometry.Rectangle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import test.AbstractRTreeTest

class ImmutableRTreeTest : AbstractRTreeTest() {

  private val factory = Geometries
  private val items = listOf(
    Item(1, 1.0, 2.0),
    Item(2, 2.0, 2.0),
    Item(3, 1.0, 1.0),
    Item(4, 0.0, 0.0),
    Item(5, -1.0, -1.0)
  )

  @Test
  fun `simple bound search`() {
    val tree = items.fold(
      RTree.builder<Int, Rectangle>().minChildren(4).maxChildren(8).create()
    ) { tree, item ->
      tree.add(item.value, factory.point(item.x, item.y))
    }
    val result = tree.search(factory.rectangle(0.0, 0.0, 2.0, 2.0)).map { it.value }
    assertThat(result).hasSize(4)
    assertThat(result).contains(1, 2, 3, 4)
    assertThat(result).doesNotContain(5)
  }

  @Test
  fun `bulk loading empty tree`() {
    val tree = RTree.builder<Int, Geometry>().create(emptyList())
    assertThat(tree.empty()).isEqualTo(true)
    assertThat(tree.size()).isEqualTo(0)
  }

  @Test
  fun `bulk loading 1 item`() {
    val tree = RTree.builder<Int, Point>().minChildren(4).maxChildren(8).create(listOf(Entry.create(1, factory.point(1.0, 1.0))))
    assertThat(tree.empty()).isEqualTo(false)
    assertThat(tree.size()).isEqualTo(1)
  }

  @Test
  fun `bulk loading and verify against entries`() {
    val entries = (1..100).map { i ->
      Entry.create(i, factory.point(i.toDouble(), i.toDouble()))
    }
    val tree = RTree.builder<Int, Point>().create(entries)
    assertThat(tree.empty()).isEqualTo(false)
    assertThat(tree.size()).isEqualTo(entries.size)
    assertThat(tree.entries().size).isEqualTo(entries.size)
  }

  @Test
  fun `search one item from bulk loading`() {
    val e = e(1)
    val tree = RTree.builder<Int, Rectangle>().create(listOf(e))
    assertThat(tree.search(r(1))).isEqualTo(listOf(e))
  }

  @Test
  fun `search for a point`() {
    val point = factory.point(1.0, 1.0)
    val tree = rtree<Int, Point>().add(1, point)
    assertThat(tree.search(point)).isEqualTo(listOf(Entry.create(1, point)))
  }

  @Test
  fun `delete entry`() {
    val e = e(1)
    val e2 = e2(1)
    val tree = rtree<Int, Rectangle>()
      .add(e)
      .add(e2)
      .remove(e.value, e.geometry, true)
    val result = tree.entries()
    assertThat(result).doesNotContain(e)
    assertThat(result).contains(e2)
  }

  @Test
  fun `delete another one`() {
    val tree = rtree<Int, Point>()
      .add(1, factory.point(123.0, 23.0))
      .remove(1, factory.point(123.0, 23.0))
    assertThat(tree.size()).isEqualTo(0)
    assertThat(tree.entries()).isEmpty()
  }

  @Test
  fun `delete a list of entries`() {
    val entries = (1..5).map { e(1) }
    val tree = rtree<Int, Rectangle>()
      .add(entries)
      .remove(entries, all = true)
    assertThat(tree.empty()).isTrue
    assertThat(tree.entries()).isEmpty()
  }

  @Test
  fun `delete full`() {
    val e = e(1)
    val tree = rtree<Int, Rectangle>()
    val a = tree.add(e)
    assertThat(a.size()).isEqualTo(1)
    val b = tree.add(e).add(e)
    assertThat(b.size()).isEqualTo(2)
    val c = tree.add(e).add(e).remove(e, all = true)
    assertThat(c.size()).isEqualTo(0)
  }

  @Test
  fun `delete partial`() {
    val e = e(1)
    val tree = rtree<Int, Rectangle>()
    val result = tree.add(e).add(e).remove(e, all = false)
    assertThat(result.size()).isEqualTo(1)
  }

  @Test
  fun `delete full using key`() {
    val e = e(1)
    val tree = rtree<Int, Rectangle>()
    val a = tree.add(e)
    assertThat(a.size()).isEqualTo(1)
    val b = tree.add(e).add(e)
    assertThat(b.size()).isEqualTo(2)
    val c = tree.add(e).add(e).remove(e.value, e.geometry, all = true)
    assertThat(c.size()).isEqualTo(0)
  }

  @Test
  fun `delete partial using key`() {
    val e = e(1)
    val tree = rtree<Int, Rectangle>()
    val result = tree.add(e).add(e).remove(e.value, e.geometry, all = false)
    assertThat(result.size()).isEqualTo(1)
  }

  @Test
  fun `delete full list of entries`() {
    val e = e(1)
    val tree = rtree<Int, Rectangle>()
    val a = tree.add(e)
    assertThat(a.size()).isEqualTo(1)
    val b = tree.add(e).add(e)
    assertThat(b.size()).isEqualTo(2)
    val c = tree.add(e).add(e).remove(listOf(e), all = true)
    assertThat(c.size()).isEqualTo(0)
  }

  @Test
  fun `delete partial list of entries`() {
    val e = e(1)
    val tree = rtree<Int, Rectangle>()
    val result = tree.add(e).add(e).remove(listOf(e), all = false)
    assertThat(result.size()).isEqualTo(1)
  }

  @Test
  fun `delete empty tree`() {
    val tree = rtree<Int, Rectangle>()
    assertThat(tree.remove(e(2)).entries()).isEmpty()
  }

  @Test
  fun `delete not existing entry`() {
    val tree = rtree<Int, Rectangle>().add(e(1))
    val result = tree.remove(e(2)).entries()
    assertThat(result[0]).isEqualTo(e(1))
  }

  private fun <T, G : Geometry> rtree(): RTree<T, G> {
    return RTree.builder<T, G>().minChildren(4).maxChildren(8).create()
  }

  @ParameterizedTest(name = "tree(maxChildren={0}, items={1}).depth = {2}")
  @CsvSource(
    "3, 1, 1",
    "3, 2, 1",
    "3, 3, 1",
    "3, 4, 2",
    "3, 8, 3",
    "3, 10, 3"
  )
  fun depth(maxChildren: Int, items: Int, depth: Int) {
    val tree = RTree
      .builder<Int, Rectangle>()
      .minChildren(1)
      .maxChildren(maxChildren)
      .create()
      .add((1..items).map { e(1) })
    assertThat(tree.depth()).isEqualTo(depth)
  }
}
