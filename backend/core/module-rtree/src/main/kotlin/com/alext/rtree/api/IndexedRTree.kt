package com.alext.rtree.api

import com.alext.rtree.core.builder.ImmutableIndexedRTreeBuilder
import com.alext.rtree.core.geometry.Geometry

/**
 * Same as [RTree] with additional inverted [index] map for entry
 */
interface IndexedRTree<T, G : Geometry> : RTree<T, G> {
  /**
   * Secondary index for entries.
   */
  val index: Map<T, Entry<T, G>>

  /**
   * Add an entry to the tree.
   * Entry with same value will overwrite existing value in [index] map.
   *
   * @param entry item to add to the R-tree.
   */
  override fun add(entry: Entry<T, G>): IndexedRTree<T, G>

  /**
   * Add a pair of value and geometry to the tree.
   * Entry with same value will overwrite existing value in [index] map.
   *
   * @param value The value of the [Entry] to be added
   * @param geometry The geometry of the [Entry] to be added
   */
  override fun add(value: T, geometry: G): IndexedRTree<T, G>

  /**
   * Add a list of entries to the tree, bulk inserting.
   * Entry with same value will overwrite existing value in [index] map.
   *
   * @param entries The list of entries to be added
   */
  override fun add(entries: Iterable<Entry<T, G>>): IndexedRTree<T, G>

  /**
   * Delete a list of entries. If `all` is false deletes only one if exists.
   * If `all` is true deletes all matching entries.
   *
   * @param entries The list of entries to be deleted
   * @param all If false deletes one if exists else deletes all
   */
  override fun remove(entries: Iterable<Entry<T, G>>, all: Boolean): IndexedRTree<T, G>

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
  override fun remove(value: T, geometry: G, all: Boolean): IndexedRTree<T, G>

  /**
   * If `all` is false deletes one entry matching the given value and
   * Geometry. If `all` is true deletes all entries matching the given
   * value and geometry. This method has no effect if the entry is not present.
   * The entry must match on both value and geometry to be deleted.
   *
   * @param entry The entry to be deleted
   * @param all If false deletes one if exists else deletes all
   */
  override fun remove(entry: Entry<T, G>, all: Boolean): IndexedRTree<T, G>

  companion object {

    fun <T, G : Geometry> builder(): Builder<T, G, IndexedRTree<T, G>> {
      return ImmutableIndexedRTreeBuilder()
    }
  }
}
