package com.alext.rtree.api

import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.misc.Stack

internal interface TreeSearch {

  /**
   * Recursively search down the tree using stack to avoid too deep
   * recursive stack.
   *
   * @param root The starting root node
   * @param intersect The intersection function of a geometry
   */
  fun <T, G : Geometry> search(root: Node<T, G>?, intersect: (Geometry) -> Boolean): List<Entry<T, G>> {
    if (root == null) {
      return emptyList()
    } else {
      val stack = Stack<Node<T, G>>()
      stack.push(root)
      val result = mutableListOf<Entry<T, G>>()
      while (!stack.empty) {
        when (val top = stack.pop()) {
          is SubTree<T, G> -> {
            if (intersect(top.geometry)) {
              val children = top.nodes
              for (child in children) {
                if (intersect(child.geometry)) {
                  stack.push(child)
                }
              }
            }
          }
          else -> {
            val leaf = (top as Leaf<T, G>)
            if (intersect(leaf.geometry)) {
              for (i in 0 until leaf.count()) {
                val entry = leaf.entry(i)
                if (intersect(entry.geometry)) {
                  result.add(entry)
                }
              }
            }
          }
        }
      }
      return result
    }
  }
}
