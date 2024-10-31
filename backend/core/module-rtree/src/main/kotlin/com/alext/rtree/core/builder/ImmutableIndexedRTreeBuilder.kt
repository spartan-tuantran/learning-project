package com.alext.rtree.core.builder

import com.alext.rtree.api.Entry
import com.alext.rtree.api.IndexedRTree
import com.alext.rtree.api.Node
import com.alext.rtree.core.Context
import com.alext.rtree.core.ImmutableIndexedRTree
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class ImmutableIndexedRTreeBuilder<T, G : Geometry> : AbstractBuilder<T, G, IndexedRTree<T, G>>() {

  @Suppress("UNCHECKED_CAST")
  private fun packing(
    objects: List<HasGeometry>,
    isLeaf: Boolean,
    size: Int,
    index: MutableMap<T, Entry<T, G>>,
    context: Context
  ): IndexedRTree<T, G> {
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
      return ImmutableIndexedRTree(
        root,
        size,
        if (isLeaf) {
          (objects as List<Entry<T, G>>).associateBy { it.value } as MutableMap<T, Entry<T, G>>
        } else {
          index
        },
        context
      )
    }

    // While building tree, intercept and capture index
    val map = linkedMapOf<T, Entry<T, G>>()
    val nodes = build(objects, isLeaf, context) { entries ->
      entries.forEach {
        map[it.value] = it
      }
    }
    map.putAll(index)
    return packing(nodes, false, size, map, context)
  }

  override fun create(): IndexedRTree<T, G> {
    setDefaultCapacity()
    return ImmutableIndexedRTree(null, 0, linkedMapOf(), Context(this))
  }

  override fun create(entries: List<Entry<T, G>>): IndexedRTree<T, G> {
    setDefaultCapacity()
    return packing(entries, true, entries.size, linkedMapOf(), Context(this))
  }
}
