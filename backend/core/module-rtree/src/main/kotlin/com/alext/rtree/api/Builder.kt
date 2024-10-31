package com.alext.rtree.api

import com.alext.rtree.core.Context
import com.alext.rtree.core.ImmutableRTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.selector.Selector
import com.alext.rtree.core.splitter.RStarSplitter
import com.alext.rtree.core.splitter.Splitter

interface Builder<T, G, R> where G : Geometry, R : RTree<T, G> {
  /**
   * The factor is used as the fill ratio during bulk loading.
   *
   * @param factor The loading factor for bulk loading
   */
  fun loadingFactor(factor: Double): Builder<T, G, R>

  /**
   * When the number of children in an R-tree node drops below this number the
   * node is deleted and the children are added on to the R-tree again.
   *
   * @param minChildren less than this number of children in a node triggers a
   * redistribution of its children.
   */
  fun minChildren(minChildren: Int): Builder<T, G, R>

  /**
   * Sets the max number of children in an R-tree node.
   *
   * @param maxChildren max number of children in R-tree node.
   */
  fun maxChildren(maxChildren: Int): Builder<T, G, R>

  /**
   * Sets the [Splitter] to use when maxChildren is reached.
   *
   * @param splitter node splitting method to use
   */
  fun splitter(splitter: Splitter): Builder<T, G, R>

  /**
   * Sets the node [Selector] which decides which branches to follow when
   * inserting or searching.
   *
   * @param selector selects the branch to follow when inserting or searching
   */
  fun selector(selector: Selector): Builder<T, G, R>

  /**
   * Sets the splitter to [RStarSplitter] and selector to
   * [Selector.R_STAR] and defaults to minChildren=10.
   */
  fun rstar(): Builder<T, G, R>

  /**
   * The r-tree context
   */
  fun context(): Context

  /**
   * Returns a new Builder instance for [ImmutableRTree]. Defaults to
   * maxChildren=128, minChildren=64, splitter=QuadraticSplitter.
   *
   * @param <T> the value type of the entries in the tree
   * @param <S> the geometry type of the entries in the tree
   * @return a new RTree instance
   */
  fun create(): R

  /**
   * Create an RTree by bulk loading, using the STR method. STR: a simple and
   * efficient algorithm for R-tree packing
   * http://ieeexplore.ieee.org/abstract/document/582015/
   *
   *
   * Note: this method mutates the input entries, the internal order of the List
   * may be changed.
   *
   *
   * @param entries entries to be added to the r-tree
   * @return a loaded RTree
   */
  fun create(entries: List<Entry<T, G>>): R
}
