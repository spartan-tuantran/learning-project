package com.alext.rtree.api

import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry

internal interface TreeDepth {

  /**
   * Return the tree depth
   */
  fun depth(): Int

  /**
   * Compute the depth of the tree from a given root
   *
   * @param root The root of the tree. 0 if null
   */
  fun <T, S : Geometry> depth(root: Node<T, S>?): Int {
    return if (root == null) {
      0
    } else {
      depth(root, 0)
    }
  }

  /**
   * Recursively compute depth for each node for both [Leaf] and [SubTree]
   *
   * @param node The node to compute
   * @param depth The current depth of this node
   */
  private fun <T, S : Geometry> depth(node: Node<T, S>, depth: Int): Int {
    return if (node is Leaf<*, *>) {
      depth + 1
    } else {
      depth((node as SubTree<T, S>).node(0), depth + 1)
    }
  }
}
