package com.alext.rtree.api

import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry

/**
 * Tree traversal to visit each node and entries of a tree.
 */
interface TreeVisitor<T, G : Geometry> {
  /**
   * Visit a leaf
   *
   * @param leaf The leaf to visit
   */
  fun visit(leaf: Leaf<T, G>)

  /**
   * Visit a subtree
   *
   * @param subTree The subtree to visit
   */
  fun visit(subTree: SubTree<T, G>)

  /**
   * Visit an entry
   *
   * @param entry The entry to visit
   */
  fun visit(entry: Entry<T, G>)
}
