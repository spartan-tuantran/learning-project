package com.alext.rtree.api

import com.alext.rtree.core.builder.ImmutableRTreeBuilder
import com.alext.rtree.core.geometry.Geometry

interface RTree<T, G : Geometry> {
  /**
   * Root of the tree
   */
  val root: Node<T, G>?

  /**
   * Return true if the tree is empty
   */
  fun empty(): Boolean

  /**
   * Returns the size of RTree include all children.
   *
   * @return the number of entries
   */
  fun size(): Int

  /**
   * Add an entry to the tree
   *
   * @param entry item to add to the R-tree.
   */
  fun add(entry: Entry<T, G>): RTree<T, G>

  /**
   * Add a pair of value and geometry to the tree.
   *
   * @param value The value of the [Entry] to be added
   * @param geometry The geometry of the [Entry] to be added
   */
  fun add(value: T, geometry: G): RTree<T, G>

  /**
   * Add a list of entries to the tree, bulk inserting
   *
   * @param entries The list of entries to be added
   */
  fun add(entries: Iterable<Entry<T, G>>): RTree<T, G>

  /**
   * Delete a list of entries. If `all` is false deletes only one if exists.
   * If `all` is true deletes all matching entries.
   *
   * @param entries The list of entries to be deleted
   * @param all If false deletes one if exists else deletes all
   */
  fun remove(entries: Iterable<Entry<T, G>>, all: Boolean = true): RTree<T, G>

  /**
   * If `all` is false deletes one entry matching the given value and
   * Geometry. If `all` is true deletes all entries matching the given
   * value and geometry. This method has no effect if the entry is not present.
   * The entry must match on both value and geometry to be deleted.
   *
   * @param value The value of the [Entry] to be deleted
   * @param geometry The geometry of the [Entry] to be deleted
   * @param all if false deletes one if exists else deletes all
   */
  fun remove(value: T, geometry: G, all: Boolean = true): RTree<T, G>

  /**
   * If `all` is false deletes one entry matching the given value and
   * Geometry. If `all` is true deletes all entries matching the given
   * value and geometry. This method has no effect if the entry is not present.
   * The entry must match on both value and geometry to be deleted.
   *
   * @param entry The entry to be deleted
   * @param all If false deletes one if exists else deletes all
   */
  fun remove(entry: Entry<T, G>, all: Boolean = false): RTree<T, G>

  /**
   * Search and return a list of entries that intersects with the given geometry.
   *
   * @param geometry The geometry to be searched
   */
  fun search(geometry: Geometry): List<Entry<T, G>>

  /**
   * Search and return a list of entries that satisfy a given predicate
   *
   * @param intersect The intersection's predicate when walking down r-tree recursively
   */
  fun search(intersect: (Geometry) -> Boolean): List<Entry<T, G>>

  /**
   * Return a list of all entries
   */
  fun entries(): List<Entry<T, G>>

  /**
   * Return the depth of the tree.
   */
  fun depth(): Int

  /**
   * Walk the tree and pass each step it visit to visitor
   *
   * @param visitor The visitor
   */
  fun walk(visitor: TreeVisitor<T, G>)

  /**
   * Adapter method to allow forwarding method of [ImmutableRTreeBuilder]
   */
  companion object {

    fun <T, G : Geometry> builder(): Builder<T, G, RTree<T, G>> {
      return ImmutableRTreeBuilder()
    }
  }
}
