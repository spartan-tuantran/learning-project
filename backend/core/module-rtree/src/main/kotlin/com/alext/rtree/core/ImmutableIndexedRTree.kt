package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.IndexedRTree
import com.alext.rtree.api.Node
import com.alext.rtree.api.TreeDepth
import com.alext.rtree.api.TreePrinter
import com.alext.rtree.api.TreeSearch
import com.alext.rtree.api.TreeVisitor
import com.alext.rtree.api.TreeWalk
import com.alext.rtree.core.geometry.Geometry

internal class ImmutableIndexedRTree<T, G : Geometry>(
  override val root: Node<T, G>? = null,
  private val size: Int = 0,
  override val index: MutableMap<T, Entry<T, G>>,
  private val context: Context
) : IndexedRTree<T, G>, TreeDepth, TreePrinter, TreeWalk, TreeSearch {

  override fun walk(visitor: TreeVisitor<T, G>) {
    walk(root, visitor)
  }

  override fun empty(): Boolean {
    return size == 0
  }

  override fun add(entry: Entry<T, G>): ImmutableIndexedRTree<T, G> {
    val node = if (root != null) {
      val nodes = root.add(entry)
      if (nodes.size == 1) {
        nodes[0]
      } else {
        SubTree.create(nodes, context)
      }
    } else {
      Leaf.create(listOf(entry), context)
    }
    index[entry.value] = entry
    return ImmutableIndexedRTree(node, size + 1, index, context)
  }

  override fun add(value: T, geometry: G): ImmutableIndexedRTree<T, G> {
    return add(Entry.create(value, geometry))
  }

  override fun add(entries: Iterable<Entry<T, G>>): ImmutableIndexedRTree<T, G> {
    return entries.fold(this) { tree, entry ->
      tree.add(entry)
    }
  }

  override fun remove(entries: Iterable<Entry<T, G>>, all: Boolean): ImmutableIndexedRTree<T, G> {
    return entries.fold(this) { tree, entry ->
      tree.remove(entry, all)
    }
  }

  override fun remove(value: T, geometry: G, all: Boolean): ImmutableIndexedRTree<T, G> {
    return remove(Entry.create(value, geometry), all)
  }

  override fun remove(entry: Entry<T, G>, all: Boolean): ImmutableIndexedRTree<T, G> {
    return if (root != null) {
      val nodeEntries = root.delete(entry, all)
      if (nodeEntries.node != null && nodeEntries.node == root) {
        this
      } else {
        index.remove(entry.value)
        ImmutableIndexedRTree(
          nodeEntries.node,
          size - nodeEntries.count - nodeEntries.entries.size,
          index,
          context
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
    return index.values.toList()
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
    internal fun <T, S : Geometry> create(root: Node<T, S>?, size: Int, context: Context): ImmutableIndexedRTree<T, S> {
      return ImmutableIndexedRTree(root, size, linkedMapOf(), context)
    }
  }
}
