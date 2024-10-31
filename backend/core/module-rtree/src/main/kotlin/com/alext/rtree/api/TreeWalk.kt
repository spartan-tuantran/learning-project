package com.alext.rtree.api

import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry

internal interface TreeWalk {

  /**
   * Walk down the tree from a node
   *
   * @param node The starting node
   * @param traversal The traversal
   */
  fun <T, G : Geometry> walk(node: Node<T, G>?, traversal: TreeVisitor<T, G>) {
    if (node != null) {
      when (node) {
        is Leaf<*, *> -> {
          val leaf = node as Leaf<T, G>
          traversal.visit(leaf)
          leaf.entries.forEach {
            traversal.visit(it)
          }
        }
        is SubTree<*, *> -> {
          val subTree = node as SubTree<T, G>
          traversal.visit(subTree)
          subTree.nodes.forEach {
            walk(it, traversal)
          }
        }
      }
    }
  }
}
