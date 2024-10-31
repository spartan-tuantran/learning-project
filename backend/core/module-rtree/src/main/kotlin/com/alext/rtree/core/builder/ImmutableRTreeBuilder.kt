package com.alext.rtree.core.builder

import com.alext.rtree.api.Entry
import com.alext.rtree.api.Node
import com.alext.rtree.api.RTree
import com.alext.rtree.core.Context
import com.alext.rtree.core.ImmutableRTree
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class ImmutableRTreeBuilder<T, G : Geometry> : AbstractBuilder<T, G, RTree<T, G>>() {

  @Suppress("UNCHECKED_CAST")
  private fun packing(
    objects: List<HasGeometry>,
    isLeaf: Boolean,
    size: Int,
    context: Context
  ): RTree<T, G> {
    val capacity = (maxChildren * loadingFactor).roundToInt()
    val nodeCount = ceil(1.0 * objects.size / capacity).toInt()
    if (nodeCount == 0) {
      return create()
    } else if (nodeCount == 1) {
      val root = if (isLeaf) {
        Leaf.create(objects as List<Entry<T, G>>, context)
      } else {
        SubTree.create(objects as List<Node<T, G>>, context)
      }
      return ImmutableRTree(root, size, context)
    }

    val nodes = build(objects, isLeaf, context, null)
    return packing(nodes, false, size, context)
  }

  override fun create(): RTree<T, G> {
    setDefaultCapacity()
    return ImmutableRTree(null, 0, Context(this))
  }

  override fun create(entries: List<Entry<T, G>>): RTree<T, G> {
    setDefaultCapacity()
    return packing(entries, true, entries.size, Context(this))
  }
}
