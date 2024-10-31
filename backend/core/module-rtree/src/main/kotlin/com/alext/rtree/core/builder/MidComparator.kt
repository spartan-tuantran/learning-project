package com.alext.rtree.core.builder

import com.alext.rtree.core.geometry.HasGeometry

/**
 * Leave space for multiple dimensions, 0 for x, 1 for y
 */
internal class MidComparator(
  private val dimension: Short
) : Comparator<HasGeometry> {
  override fun compare(a: HasGeometry, b: HasGeometry): Int {
    return mid(a).compareTo(mid(b))
  }

  private fun mid(h: HasGeometry): Double {
    val mbr = h.geometry.mbr
    return if (dimension.toInt() == 0) {
      (mbr.x1 + mbr.x2) / 2
    } else {
      (mbr.y1 + mbr.y2) / 2
    }
  }
}
