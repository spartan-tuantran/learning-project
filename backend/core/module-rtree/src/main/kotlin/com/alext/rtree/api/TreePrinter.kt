package com.alext.rtree.api

import com.alext.rtree.core.Leaf
import com.alext.rtree.core.SubTree
import com.alext.rtree.core.geometry.Geometry

internal interface TreePrinter {

  /**
   * Returns a human readable form of the RTree. Here's an example:
   *
   * mbr=Rectangle [x1=10.0, y1=4.0, x2=62.0, y2=85.0]
   * mbr=Rectangle [x1=28.0, y1=4.0, x2=34.0, y2=85.0]
   * entry=Entry [value=2, geometry=Point [x=29.0, y=4.0]]
   * entry=Entry [value=1, geometry=Point [x=28.0, y=19.0]]
   * entry=Entry [value=4, geometry=Point [x=34.0, y=85.0]]
   * mbr=Rectangle [x1=10.0, y1=45.0, x2=62.0, y2=63.0]
   * entry=Entry [value=5, geometry=Point [x=62.0, y=45.0]]
   * entry=Entry [value=3, geometry=Point [x=10.0, y=63.0]]
   */
  fun <T, G : Geometry> toString(node: Node<T, G>, margin: String): String {
    return buildString {
      append(margin)
      append("mbr=")
      append(node.geometry)
      append('\n')
      if (node is SubTree<*, *>) {
        val n = node as SubTree<T, G>
        for (i in 0 until n.count()) {
          val child = n.node(i)
          append(toString(child, "$margin "))
        }
      } else {
        val leaf = node as Leaf<T, G>
        for (entry in leaf.entries) {
          append(margin)
          append(" ")
          append("entry=")
          append(entry)
          append('\n')
        }
      }
    }
  }
}
