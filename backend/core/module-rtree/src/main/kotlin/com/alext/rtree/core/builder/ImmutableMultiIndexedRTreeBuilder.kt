package com.alext.rtree.core.builder

import com.alext.rtree.api.Entry
import com.alext.rtree.api.MultiIndexedRTree
import com.alext.rtree.api.Node
import com.alext.rtree.core.Context
import com.alext.rtree.core.ImmutableMultiIndexedRTree
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class ImmutableMultiIndexedRTreeBuilder<T, G : Geometry> : AbstractBuilder<T, G, MultiIndexedRTree<T, G>>() {

  @Suppress("UNCHECKED_CAST")
  private fun packing(
    objects: List<HasGeometry>,
    isLeaf: Boolean,
    size: Int,
    index: MutableMap<T, MutableList<Entry<T, G>>>,
    context: Context
  ): MultiIndexedRTree<T, G> {
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
      return ImmutableMultiIndexedRTree(
        root,
        size,
        if (isLeaf) {
          (objects as List<Entry<T, G>>).groupBy { it.value } as MutableMap<T, MutableList<Entry<T, G>>>
        } else {
          index
        },
        context
      )
    }

    // While building tree, intercept and capture index
    val map: MutableMap<T, MutableList<Entry<T, G>>> = linkedMapOf()
    val nodes = build(objects, isLeaf, context) { entries ->
      entries.forEach {
        val list = map.computeIfAbsent(it.value) {
          mutableListOf()
        }
        list.add(it)
      }
    }
    map.putAll(index)
    return packing(nodes, false, size, map, context)
  }

  override fun create(): MultiIndexedRTree<T, G> {
    setDefaultCapacity()
    return ImmutableMultiIndexedRTree(null, 0, linkedMapOf(), Context(this))
  }

  override fun create(entries: List<Entry<T, G>>): MultiIndexedRTree<T, G> {
    setDefaultCapacity()
    return packing(entries, true, entries.size, linkedMapOf(), Context(this))
  }
}
