package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.api.Node
import com.alext.rtree.api.NodeEntry
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.misc.remove
import com.alext.rtree.misc.replace

/**
 * A subtree contains a list of nodes
 */
interface SubTree<T, S : Geometry> : Node<T, S> {

  /**
   * Return a node at index i
   *
   * @param i The node index 0-based
   */
  fun node(i: Int): Node<T, S>

  /**
   * A list of children nodes.
   */
  val nodes: List<Node<T, S>>

  companion object Factory {

    fun <T, S : Geometry> create(nodes: List<Node<T, S>>, context: Context): SubTree<T, S> {
      return object : SubTree<T, S> {
        override val nodes: List<Node<T, S>> = nodes
        override val context: Context = context
        override val geometry: Geometry = Geometries.mbr(nodes)

        override fun count(): Int {
          return nodes.size
        }

        override fun add(entry: Entry<T, S>): List<Node<T, S>> {
          val child = context.selector.select(entry.geometry.mbr, this.nodes)
          val list = child.add(entry)
          val children2 = nodes.replace(child, list)
          return if (children2.size <= context.maxChildren) {
            listOf(create(children2, context) as Node<T, S>)
          } else {
            val pair = context.splitter.split(children2, context.minChildren)
            pair.createSubtrees(context)
          }
        }

        /**
         * The result of performing a delete of the given entry from this node
         * will be that zero or more entries will be needed to be added back to
         * the root of the tree (because num entries of their node fell below minChildren)
         * - zero or more children will need to be removed from this node,
         * - zero or more nodes to be added as children to this node (because entries have been
         * deleted from them and they still have enough members to be active)
         */
        override fun delete(entry: Entry<T, S>, all: Boolean): NodeEntry<T, S> {
          val entriesToAdd = ArrayList<Entry<T, S>>()
          val nodesToRemove = ArrayList<Node<T, S>>()
          val nodesToAdd = ArrayList<Node<T, S>>()
          var deletedCount = 0
          for (child in this.nodes) {
            if (entry.geometry.intersects(child.geometry.mbr)) {
              val result = child.delete(entry, all)
              if (result.node != null) {
                if (result.node != child) {
                  // deletion occurred and child is above minChildren so
                  // we update it
                  nodesToAdd.add(result.node)
                  nodesToRemove.add(child)
                  entriesToAdd.addAll(result.entries)
                  deletedCount += result.count
                  if (!all) {
                    break
                  }
                }
                // else nothing was deleted from that child
              } else {
                // deletion occurred and brought child below minChildren
                // so we redistribute its entries
                nodesToRemove.add(child)
                entriesToAdd.addAll(result.entries)
                deletedCount += result.count
                if (!all) {
                  break
                }
              }
            }
          }

          return if (nodesToRemove.isEmpty()) {
            NodeEntry(this, emptyList(), 0)
          } else {
            val newNodes = nodes.remove(nodesToRemove)
            val result = newNodes + nodesToAdd
            if (result.isEmpty()) {
              NodeEntry(null, entriesToAdd, deletedCount)
            } else {
              NodeEntry(create(result, context), entriesToAdd, deletedCount)
            }
          }
        }

        override fun node(i: Int): Node<T, S> {
          return nodes[i]
        }

        override fun toString(): String {
          return "SubTree[children=$nodes, geometry=$geometry]"
        }
      }
    }
  }
}

/**
 * Create two subtrees, one for each group
 *
 * @param context The context
 */
internal fun <T, S : Geometry> GroupPair<Node<T, S>>.createSubtrees(context: Context): List<Node<T, S>> {
  return ArrayList<Node<T, S>>(2).apply {
    add(SubTree.create(first.items, context))
    add(SubTree.create(second.items, context))
  }
}
