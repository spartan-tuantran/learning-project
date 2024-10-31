package com.alext.rtree.api

import com.alext.rtree.core.geometry.Geometry

/**
 * Used for tracking deletions through recursive calls.
 * @param node If null = whole node was deleted present = either an unchanged node because of no removal or
 *                the newly created node without the deleted entry
 * @param entries The entries from nodes that dropped below minChildren in size and thus their entries are to be
 *                redistributed (re-added to the tree)
 * @param count The count of the number of entries removed
 */
class NodeEntry<T, S : Geometry>(
  val node: Node<T, S>? = null,
  val entries: List<Entry<T, S>>,
  val count: Int
) {

  override fun toString(): String {
    return "NodeEntry[node=$node, entries=$entries, count=$count]"
  }
}
