@file:Suppress("UNCHECKED_CAST")

package com.alext.rtree.core.builder

import com.alext.rtree.api.Builder
import com.alext.rtree.api.Entry
import com.alext.rtree.api.Node
import com.alext.rtree.api.RTree
import com.alext.rtree.core.Context
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry
import com.alext.rtree.core.selector.Selector
import com.alext.rtree.core.splitter.QuadraticSplitter
import com.alext.rtree.core.splitter.RStarSplitter
import com.alext.rtree.core.splitter.Splitter
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal abstract class AbstractBuilder<T, G, R> : Builder<T, G, R> where G : Geometry, R : RTree<T, G> {

  companion object {
    /**
     * According to http://dbs.mathematik.uni-marburg.de/publications/myPapers
     * /1990/BKSS90.pdf (R*-tree paper), best filling ratio is 0.4 for both
     * quadratic split and R*-tree split.
     */
    private const val DEFAULT_FILLING_FACTOR: Double = 0.4
    private const val DEFAULT_LOADING_FACTOR: Double = 0.7

    /**
     * Benchmarks show that this is a good choice for up to O(10,000) entries when
     * using Quadratic splitter (Guttman).
     */
    const val MAX_CHILDREN_DEFAULT_GUTTMAN = 4

    /**
     * Benchmarks show that this is the sweet spot for up to O(10,000) entries when
     * using R*-tree heuristics.
     */
    const val MAX_CHILDREN_DEFAULT_STAR = 4
  }

  internal var maxChildren: Int = -1
  internal var minChildren: Int = -1
  internal var selector: Selector = Selector.MINIMAL_AREA_INCREASE
  internal var splitter: Splitter = QuadraticSplitter()
  internal var loadingFactor: Double = 0.0
  internal var star = false

  init {
    loadingFactor = DEFAULT_LOADING_FACTOR
  }

  final override fun loadingFactor(factor: Double): AbstractBuilder<T, G, R> {
    this.loadingFactor = factor
    return this
  }

  final override fun minChildren(minChildren: Int): AbstractBuilder<T, G, R> {
    this.minChildren = minChildren
    return this
  }

  final override fun maxChildren(maxChildren: Int): AbstractBuilder<T, G, R> {
    this.maxChildren = maxChildren
    return this
  }

  final override fun splitter(splitter: Splitter): AbstractBuilder<T, G, R> {
    this.splitter = splitter
    return this
  }

  final override fun selector(selector: Selector): AbstractBuilder<T, G, R> {
    this.selector = selector
    return this
  }

  final override fun rstar(): AbstractBuilder<T, G, R> {
    selector = Selector.R_STAR
    splitter = RStarSplitter()
    star = true
    return this
  }

  final override fun context(): Context {
    return Context(this)
  }

  /**
   * Build a list of tree nodes from a given list of [objects]
   */
  protected fun build(
    objects: List<HasGeometry>,
    isLeaf: Boolean,
    context: Context,
    interceptor: ((List<Entry<T, G>>) -> Unit)? = null
  ): List<HasGeometry> {
    val capacity = (maxChildren * loadingFactor).roundToInt()
    val nodeCount = ceil(1.0 * objects.size / capacity).toInt()
    val nodePerSlice = ceil(sqrt(nodeCount.toDouble())).toInt()
    val sliceCapacity = nodePerSlice * capacity
    val sliceCount = ceil(1.0 * objects.size / sliceCapacity).toInt()
    val sorted = objects.sortedWith(MidComparator(0.toShort()))
    val nodes = ArrayList<Node<T, G>>(nodeCount)
    for (s in 0 until sliceCount) {
      val slice = sorted.subList(s * sliceCapacity, min((s + 1) * sliceCapacity, sorted.size)).sortedWith(MidComparator(1.toShort()))
      var i = 0
      while (i < slice.size) {
        nodes.add(
          if (isLeaf) {
            val entries = slice.subList(i, min(slice.size, i + capacity))
            interceptor?.invoke(entries as List<Entry<T, G>>)
            Leaf.create(entries as List<Entry<T, G>>, context)
          } else {
            val children = slice.subList(i, min(slice.size, i + capacity))
            SubTree.create(children as List<Node<T, G>>, context)
          }
        )
        i += capacity
      }
    }
    return nodes
  }

  /**
   * Set default value for min child and max child if not set
   */
  protected fun setDefaultCapacity() {
    if (maxChildren == -1) {
      maxChildren = if (star) {
        MAX_CHILDREN_DEFAULT_STAR
      } else {
        MAX_CHILDREN_DEFAULT_GUTTMAN
      }
    }
    if (minChildren == -1) {
      minChildren = (maxChildren * DEFAULT_FILLING_FACTOR).roundToInt()
    }
  }
}
