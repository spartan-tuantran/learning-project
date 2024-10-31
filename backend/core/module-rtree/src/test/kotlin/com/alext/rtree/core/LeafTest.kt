package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.core.geometry.Rectangle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LeafTest {

  private val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(4).context()
  private val factory = Geometries
  private val r1 = factory.rectangle(0.0, 0.0, 0.0, 1.0)
  private val r2 = factory.rectangle(0.0, 0.0, 1.0, 0.0)
  private val r3 = factory.rectangle(0.0, 1.0, 0.0, 0.0)
  private val r4 = factory.rectangle(1.0, 0.0, 0.0, 0.0)
  private val e1 = Entry.create(1, r1)
  private val e2 = Entry.create(2, r2)
  private val e3 = Entry.create(3, r3)
  private val e4 = Entry.create(4, r4)

  @Test
  fun `create a leaf`() {
    val leaf = Leaf.create(
      entries = listOf(e1, e2),
      context = context
    )
    assertThat(r1.merge(r2)).isEqualTo(leaf.geometry.mbr)
    assertThat(leaf.entries).hasSize(2)
  }

  @Test
  fun `leaf entry at index`() {
    val leaf = Leaf.create(
      entries = listOf(e1, e2),
      context = context
    )
    assertThat(leaf.entry(0).value).isEqualTo(1)
    assertThat(leaf.entry(0).geometry).isEqualTo(r1)
    assertThat(leaf.entry(1).value).isEqualTo(2)
    assertThat(leaf.entry(1).geometry).isEqualTo(r2)
  }

  @Test
  fun `do not split when tree is less than max children`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(3).context()
    assertThat(
      Leaf.create(
        entries = listOf(e1, e2),
        context = context
      )
        .add(e3)
    ).hasSize(1)
  }

  @Test
  fun `split when tree is greater than max children`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(3).context()
    assertThat(
      Leaf.create(
        entries = listOf(e1, e2, e3),
        context = context
      )
        .add(e4)
    ).hasSize(2)
  }

  @Test
  fun `create leaves from group`() {
    val pair = GroupPair.create(listOf(e1, e2), listOf(e3, e4))
    val result = pair.createLeaves(context)
    assertThat(result).hasSize(2)
    val l1 = result[0] as Leaf<Int, Rectangle>
    val l2 = result[1] as Leaf<Int, Rectangle>
    assertThat(l1.entry(0)).isEqualTo(e1)
    assertThat(l1.entry(1)).isEqualTo(e2)
    assertThat(l2.entry(0)).isEqualTo(e3)
    assertThat(l2.entry(1)).isEqualTo(e4)
  }

  @Test
  fun `delete() entry from leaf not all`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(4).context()
    val leaf = Leaf.create(
      entries = listOf(e1, e2, e3, e4, e1),
      context = context
    )
    assertThat(leaf.delete(e1, all = false).entries).hasSize(0)
    assertThat(leaf.delete(e1, all = false).count).isEqualTo(1)
  }

  @Test
  fun `delete() all entry from leaf`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(3).maxChildren(4).context()
    val leaf = Leaf.create(
      entries = listOf(e1, e1, e3, e4, e1),
      context = context
    )
    assertThat(leaf.delete(e1, all = true).entries).hasSize(2)
    assertThat(leaf.delete(e1, all = true).count).isEqualTo(3)
  }
}
