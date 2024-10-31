package com.alext.rtree.core.splitter

import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.core.geometry.HasGeometry

/**
 * Reference
 * http://donar.umiacs.umd.edu/quadtree/docs/rtree_split_rules.html
 */
interface Splitter {
  /**
   * Splits a list of items into two lists of at least minSize.
   *
   * @param items list of items to split
   * @param minSize min size of each list
   */
  fun <T : HasGeometry> split(items: List<T>, minSize: Int): GroupPair<T>
}
