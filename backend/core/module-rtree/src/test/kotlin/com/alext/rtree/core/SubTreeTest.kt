package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.misc.add
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SubTreeTest {

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
  fun `create a subtree`() {
    val tree = SubTree.create(
      nodes = listOf(
        Leaf.create(listOf(e1, e2), context),
        Leaf.create(listOf(e3, e4), context)
      ),
      context = context
    )
    assertThat(tree.nodes).hasSize(2)
    assertThat(tree.count()).isEqualTo(2)
    assertThat(tree.geometry).isEqualTo(factory.mbr(listOf(r1, r2, r3, r4)))
  }

  @Test
  fun `subtree node at index`() {
    val tree = SubTree.create(
      nodes = listOf(
        Leaf.create(listOf(e1, e2), context),
        Leaf.create(listOf(e3, e4), context)
      ),
      context = context
    )
    val l1 = tree.node(0) as Leaf<Int, Rectangle>
    assertThat(l1.entry(0)).isEqualTo(e1)
    assertThat(l1.entry(1)).isEqualTo(e2)
    val l2 = tree.node(1) as Leaf<Int, Rectangle>
    assertThat(l2.entry(0)).isEqualTo(e3)
    assertThat(l2.entry(1)).isEqualTo(e4)
  }

  @Test
  fun `add() entry to subtree force split`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(1).context()
    val tree = SubTree.create(
      nodes = listOf(
        Leaf.create(listOf(e1), context),
        Leaf.create(listOf(e2), context)
      ),
      context = context
    )
    assertThat(tree.add(e3).add(e4)).hasSize(3)
  }

  @Test
  fun `add() entry to subtree single child`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(3).context()
    val tree = SubTree.create(
      nodes = listOf(
        Leaf.create(listOf(e1, e2, e3, e4), context),
        Leaf.create(listOf(e2), context)
      ),
      context = context
    )
    assertThat(tree.add(e3)).hasSize(1)
  }

  @Test
  fun `delete() an entry from subtree`() {
    val context = RTree.builder<Int, Rectangle>().minChildren(1).maxChildren(3).context()
    val tree = SubTree.create(
      nodes = listOf(
        Leaf.create(listOf(e1, e2, e3, e4), context),
        Leaf.create(listOf(e2), context)
      ),
      context = context
    )
    assertThat(tree.delete(e2, all = true).count).isEqualTo(2)
    assertThat(tree.delete(e2, all = false).count).isEqualTo(1)
  }
}
