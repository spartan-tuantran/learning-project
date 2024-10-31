package com.alext.rtree.core.geometry

import com.alext.rtree.document.Notes

/**
 * A point that contains x and y coordinates
 */
@Notes("all implementation must implement equals() and hashCode()")
interface Point : Rectangle {
  val x: Double
  val y: Double
}
