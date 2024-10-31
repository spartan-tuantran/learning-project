@file:Suppress("ClassName")

package com.alext.rtree.core.selector

import com.alext.rtree.api.Node
import com.alext.rtree.core.Leaf
import com.alext.rtree.core.geometry.Geometry

/**
 * The heuristic used on insert to select which node to add an Entry to.
 */
interface Selector {
  /**
   * Returns the node from a list of nodes that an object with the given
   * geometry would be added to.
   *
   * @param geometry The geometry
   * @param nodes The nodes to select from
   */
  fun <T, S : Geometry> select(geometry: Geometry, nodes: List<Node<T, S>>): Node<T, S>

  /**
   * Selector that uses minimal area increase as heuristic
   */
  object MINIMAL_AREA_INCREASE : Selector {
    override fun <T, S : Geometry> select(geometry: Geometry, nodes: List<Node<T, S>>): Node<T, S> {
      val mbr = geometry.mbr
      return nodes.minWithOrNull { g1, g2 ->
        var value = areaIncrease(mbr, g1).compareTo(areaIncrease(mbr, g2))
        if (value == 0) {
          value = area(mbr, g1).compareTo(area(mbr, g2))
        }
        value
      } ?: throw IllegalStateException("geometry.nodes is empty!")
    }
  }

  /**
   * Selector that uses minimal overlap area then fallback to [MINIMAL_AREA_INCREASE]
   */
  object MINIMAL_OVERLAP_AREA : Selector {
    override fun <T, S : Geometry> select(geometry: Geometry, nodes: List<Node<T, S>>): Node<T, S> {
      val mbr = geometry.mbr
      return nodes.minWithOrNull { a, b ->
        var value = areaOverlap(mbr, nodes, a).compareTo(areaOverlap(mbr, nodes, b))
        if (value == 0) {
          value = areaIncrease(mbr, a).compareTo(areaIncrease(mbr, b))
          if (value == 0) {
            value = area(mbr, a).compareTo(area(mbr, b))
          }
        }
        value
      } ?: throw IllegalStateException("geometry.nodes is empty!")
    }
  }

  /**
   * Selector that uses both [MINIMAL_OVERLAP_AREA] for leaf nodes and [MINIMAL_AREA_INCREASE] for others.
   */
  object R_STAR : Selector {
    override fun <T, S : Geometry> select(geometry: Geometry, nodes: List<Node<T, S>>): Node<T, S> {
      val leaf = nodes.first() is Leaf
      return if (leaf) {
        MINIMAL_OVERLAP_AREA.select(geometry, nodes)
      } else {
        MINIMAL_AREA_INCREASE.select(geometry, nodes)
      }
    }
  }
}
