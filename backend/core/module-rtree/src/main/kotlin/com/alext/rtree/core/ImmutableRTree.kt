package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.Node
import com.alext.rtree.api.RTree
import com.alext.rtree.api.TreeDepth
import com.alext.rtree.api.TreePrinter
import com.alext.rtree.api.TreeSearch
import com.alext.rtree.api.TreeVisitor
import com.alext.rtree.api.TreeWalk
import com.alext.rtree.core.geometry.Geometry

internal class ImmutableRTree<T, G : Geometry>(
  override val root: Node<T, G>? = null,
  private val size: Int = 0,
  private val context: Context
) : RTree<T, G>, TreeDepth, TreePrinter, TreeWalk, TreeSearch {

  override fun walk(visitor: TreeVisitor<T, G>) {
    walk(root, visitor)
  }

  override fun empty(): Boolean {
    return size == 0
  }

  override fun add(entry: Entry<T, G>): ImmutableRTree<T, G> {
    return if (root != null) {
      val nodes = root.add(entry)
      val node = if (nodes.size == 1) {
        nodes[0]
      } else {
        SubTree.create(nodes, context)
      }
      ImmutableRTree(node, size + 1, context)
    } else {
      val node = Leaf.create(listOf(entry), context)
      ImmutableRTree(node, size + 1, context)
    }
  }

  override fun add(value: T, geometry: G): ImmutableRTree<T, G> {
    return add(Entry.create(value, geometry))
  }

  override fun add(entries: Iterable<Entry<T, G>>): ImmutableRTree<T, G> {
    return entries.fold(this) { tree, entry ->
      tree.add(entry)
    }
  }

  override fun remove(entries: Iterable<Entry<T, G>>, all: Boolean): ImmutableRTree<T, G> {
    return entries.fold(this) { tree, entry ->
      tree.remove(entry, all)
    }
  }

  override fun remove(value: T, geometry: G, all: Boolean): ImmutableRTree<T, G> {
    return remove(Entry.create(value, geometry), all)
  }

  override fun remove(entry: Entry<T, G>, all: Boolean): ImmutableRTree<T, G> {
    return if (root != null) {
      val nodeEntries = root.delete(entry, all)
      if (nodeEntries.node != null && nodeEntries.node == root) {
        this
      } else {
        ImmutableRTree(
          root = nodeEntries.node,
          size = size - nodeEntries.count - nodeEntries.entries.size,
          context = context
        ).add(nodeEntries.entries)
      }
    } else {
      this
    }
  }

  override fun search(geometry: Geometry): List<Entry<T, G>> {
    return search { g ->
      g.intersects(geometry.mbr)
    }
  }

  override fun entries(): List<Entry<T, G>> {
    return search(ALWAYS_TRUE)
  }

  override fun search(intersect: (Geometry) -> Boolean): List<Entry<T, G>> {
    return search(root, intersect)
  }

  override fun size(): Int {
    return size
  }

  override fun toString(): String {
    return if (root == null) {
      ""
    } else {
      toString(root, "")
    }
  }

  override fun depth(): Int {
    return depth(root)
  }

  companion object {
    /**
     * Use to retrieve all entries in the tree
     */
    private val ALWAYS_TRUE = { _: Geometry -> true }

    internal fun <T, S : Geometry> create(root: Node<T, S>?, size: Int, context: Context): ImmutableRTree<T, S> {
      return ImmutableRTree(root, size, context)
    }
  }
}
