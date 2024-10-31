package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.RTree
import com.alext.rtree.api.TreeVisitor
import com.alext.rtree.core.geometry.Rectangle
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import test.AbstractRTreeTest
import test.ConsoleLogging

class TreeWalkTest : AbstractRTreeTest() {

  private val traversal = mockk<TreeVisitor<Int, Rectangle>>(relaxed = true)

  @Test
  fun `walk empty tree`() {
    val tree = RTree
      .builder<Int, Rectangle>().minChildren(1)
      .maxChildren(4)
      .create()

    tree.walk(traversal)
    verify { traversal wasNot Called }
  }

  @Test
  fun `walk a tree single node`() {
    val tree = RTree
      .builder<Int, Rectangle>().minChildren(1)
      .maxChildren(2).create()
      .add(1, r(1))

    tree.walk(Printer)
    tree.walk(traversal)
    verify(exactly = 1) {
      traversal.visit(any<Entry<Int, Rectangle>>())
    }
    verify(exactly = 1) {
      traversal.visit(any<Leaf<Int, Rectangle>>())
    }
  }

  @Test
  fun `walk a tree single subtree, single leaf`() {
    val tree = RTree
      .builder<Int, Rectangle>().minChildren(1)
      .maxChildren(1)
      .create()
      .add(1, r(1))
      .add(2, r(2))

    tree.walk(Printer)
    tree.walk(traversal)
    verify(exactly = 2) {
      traversal.visit(any<Entry<Int, Rectangle>>())
    }
    verify(exactly = 2) {
      traversal.visit(any<Leaf<Int, Rectangle>>())
    }
    verify(exactly = 1) {
      traversal.visit(any<SubTree<Int, Rectangle>>())
    }
  }

  @Test
  fun `walk a tree multiple subtrees, multiple leaves`() {
    val tree = RTree
      .builder<Int, Rectangle>().minChildren(1)
      .maxChildren(2).create()
      .add(1, r(1))
      .add(2, r(2))
      .add(3, r(3))
      .add(4, r(4))
      .add(5, r(5))

    tree.walk(Printer)
    tree.walk(traversal)
    verify(exactly = 5) {
      traversal.visit(any<Entry<Int, Rectangle>>())
    }
    verify(exactly = 3) {
      traversal.visit(any<Leaf<Int, Rectangle>>())
    }
    verify(exactly = 3) {
      traversal.visit(any<SubTree<Int, Rectangle>>())
    }
  }

  object Printer : TreeVisitor<Int, Rectangle>, ConsoleLogging {
    override fun visit(leaf: Leaf<Int, Rectangle>) {
      green("$leaf")
    }

    override fun visit(subTree: SubTree<Int, Rectangle>) {
      purple("$subTree")
    }

    override fun visit(entry: Entry<Int, Rectangle>) {
      red("$entry")
    }
  }
}
