package com.alext.rtree.renderer

import com.alext.rtree.api.Node
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle

class RectangleView(
  val rectangle: Rectangle,
  val depth: Int
)

internal fun <T, S : Geometry> Node<T, S>.viewsAt(depth: Int): List<RectangleView> {
  val list = ArrayList<RectangleView>()
  list.add(RectangleView(geometry.mbr, depth))
  if (this is Leaf<*, *>) {
    val leaf = this as Leaf<T, S>
    for (entry in leaf.entries) {
      list.add(RectangleView(entry.geometry.mbr, depth + 2))
    }
  } else {
    val tree = this as SubTree<T, S>
    for (i in 0 until tree.count()) {
      list.addAll(tree.node(i).viewsAt(depth + 1))
    }
  }
  return list
}
