package com.alext.rtree.misc.quadtree

import com.alext.rtree.document.NotThreadSafe

/**
 * [QuadTree] contains 4 children each guard against one of the four quadrants
 * -------------
 * |  0  |  1  |
 * -------------
 * |  2  |  3  |
 * ------------
 */
@NotThreadSafe
class QuadTree<T : Element<*>> {

  companion object {
    /** The maximum number of elements of each quadrant in tree before splitting */
    private const val MAX_ELEMENTS = 4

    /** The maximum depth of the tree */
    private const val MAX_DEPTH = 32
  }

  /** The bound of this quad */
  private val bound: Bound

  /** The depth of this quad */
  private val depth: Int

  /** Contains all data point */
  private var elements: MutableList<T>? = null

  /** 4 children, one for each quadrant */
  private var children: Array<QuadTree<T>>? = null

  constructor(bound: Bound, depth: Int) {
    this.bound = bound
    this.depth = depth
  }

  constructor(bound: Bound) : this(bound, 0)

  constructor(minX: Double, maxX: Double, minY: Double, maxY: Double, depth: Int) :
    this(Bound(minX, maxX, minY, maxY), depth)

  /**
   * Add element to quad tree and return true
   * if it's added or within bound.
   *
   * @param elem An element to be added
   */
  fun add(elem: T): Boolean {
    val point = elem.point
    return if (bound.contains(point)) {
      insert(point.x, point.y, elem)
      true
    } else {
      false
    }
  }

  /**
   * Remove an element from quad tree and return true
   * if it's removed or within bound.
   *
   * @param elem An element to be removed
   */
  fun remove(elem: T): Boolean {
    val point = elem.point
    return if (bound.contains(point.x, point.y)) {
      remove(point.x, point.y, elem)
    } else {
      false
    }
  }

  /**
   * Remove all elements from the tree
   */
  fun clear() {
    children = null
    elements?.clear()
  }

  /**
   * Return the size of this tree.
   */
  fun size(): Int {
    var self = elements?.size ?: 0
    children?.let {
      it.forEach { child ->
        self += child.size()
      }
    }
    return self
  }

  /**
   * Search all elements within this target bound
   *
   * @param target The target bound to be searched
   */
  fun search(target: Bound): List<T> {
    val result = mutableListOf<T>()
    search(target, result)
    return result
  }

  private fun search(target: Bound, results: MutableCollection<T>) {
    if (!bound.intersects(target)) {
      return
    }

    // Perform search on all children
    children?.let {
      for (quad in it) {
        quad.search(target, results)
      }
      return
    }

    // If child has any element, drill down
    elements?.let { elements ->
      // If target contains bound, just add all
      if (target.contains(bound)) {
        results.addAll(elements)
      } else {
        // Need to go through individual element one by one
        for (item in elements) {
          if (target.contains(item.point)) {
            results.add(item)
          }
        }
      }
    }
  }

  private fun remove(x: Double, y: Double, item: T): Boolean {
    return if (children != null) {
      children!![bound.quadrant(x, y)].remove(x, y, item)
    } else {
      elements?.remove(item) ?: false
    }
  }

  private fun insert(x: Double, y: Double, elem: T) {
    children?.let {
      it[bound.quadrant(x, y)].insert(x, y, elem)
      return
    }

    if (elements == null) {
      elements = ArrayList()
    }
    elements?.add(elem)
    if ((elements?.size ?: 0) > MAX_ELEMENTS && depth < MAX_DEPTH) {
      split()
    }
  }

  private fun split() {
    children = arrayOf(
      QuadTree(bound.minX, bound.midX, bound.minY, bound.midY, depth + 1),
      QuadTree(bound.midX, bound.maxX, bound.minY, bound.midY, depth + 1),
      QuadTree(bound.minX, bound.midX, bound.midY, bound.maxY, depth + 1),
      QuadTree(bound.midX, bound.maxX, bound.midY, bound.maxY, depth + 1)
    )

    // Make a copy
    val copy = elements
    // Reset previous items to null
    elements = null
    // Reinsert into child quads
    copy?.forEach {
      insert(it.point.x, it.point.y, it)
    }
  }
}
