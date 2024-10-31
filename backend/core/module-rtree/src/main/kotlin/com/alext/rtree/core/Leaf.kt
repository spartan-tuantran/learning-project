package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.Node
import com.alext.rtree.api.NodeEntry
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.misc.add

/**
 * A leaf contains a list of entries
 */
interface Leaf<T, S : Geometry> : Node<T, S> {

  /**
   * All the entries of this leaf
   */
  val entries: List<Entry<T, S>>

  /**
   * Returns an entry at index i
   *
   * @param i The entry index 0-based
   */
  fun entry(i: Int): Entry<T, S>

  companion object Factory {

    fun <T, S : Geometry> create(entries: List<Entry<T, S>>, context: Context): Leaf<T, S> {
      return object : Leaf<T, S> {
        override val entries = entries
        override val context: Context = context
        override val geometry: Geometry = Geometries.mbr(entries)

        override fun count(): Int {
          return entries.size
        }

        @Suppress("UNCHECKED_CAST")
        override fun add(entry: Entry<T, S>): List<Node<T, S>> {
          val result = entries.add(entry)
          return if (result.size <= context.maxChildren) {
            listOf(create(result, context) as Node<T, S>)
          } else {
            context.splitter
              .split(result, context.minChildren)
              .createLeaves(context)
          }
        }

        override fun delete(entry: Entry<T, S>, all: Boolean): NodeEntry<T, S> {
          return if (entry !in entries) {
            NodeEntry(this, emptyList(), 0)
          } else {
            val result = ArrayList(entries)
            result.remove(entry)
            var numDeleted = 1
            // keep deleting if all specified
            while (all && result.remove(entry)) {
              numDeleted += 1
            }
            if (result.size >= context.minChildren) {
              val node = create(result, context)
              NodeEntry(node, emptyList(), numDeleted)
            } else {
              NodeEntry(null, result, numDeleted)
            }
          }
        }

        override fun entry(i: Int): Entry<T, S> {
          return entries[i]
        }

        override fun toString(): String {
          return "Leaf[entries=$entries, geometry=$geometry]"
        }
      }
    }
  }
}

/**
 * Create two leaves, one for each group
 *
 * @param context The context
 */
internal fun <T, S : Geometry> GroupPair<Entry<T, S>>.createLeaves(context: Context): List<Node<T, S>> {
  return ArrayList<Node<T, S>>(2).apply {
    add(Leaf.create(first.items, context))
    add(Leaf.create(second.items, context))
  }
}
