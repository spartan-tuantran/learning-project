package com.alext.rtree.core.splitter

import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.core.geometry.HasGeometry
import java.util.Collections

class RStarSplitter : Splitter {

  private val comparator: Comparator<GroupPair<*>>

  init {
    this.comparator = Comparator { p1, p2 ->
      // check overlap first then areaSum
      val value = overlap(p1).compareTo(overlap(p2))
      if (value == 0) {
        p1.areaSum.compareTo(p2.areaSum)
      } else {
        value
      }
    }
  }

  override fun <T : HasGeometry> split(items: List<T>, minSize: Int): GroupPair<T> {
    // sort nodes into increasing x, calculate min overlap where both groups
    // have more than minChildren

    // compute S the sum of all margin-values of the lists above
    // the list with the least S is then used to find minimum overlap

    var pairs: List<GroupPair<T>>? = null
    var lowestMarginSum = Double.MAX_VALUE
    var list: List<T>? = null
    for (sortType in SortType.values()) {
      if (list == null) {
        list = ArrayList(items)
      }
      Collections.sort(list, sortType.comparator)
      val p = pairsOf(minSize, list)
      val marginSum = p.sumOf { it.marginSum }
      if (marginSum < lowestMarginSum) {
        lowestMarginSum = marginSum
        pairs = p
        // because p uses subViews of list we need to create a new one
        // for further comparisons
        list = null
      }
    }
    return Collections.min(pairs!!, comparator)
  }

  @Suppress("unused")
  private enum class SortType(val comparator: Comparator<HasGeometry>) {
    X_LOWER(
      Comparator<HasGeometry> { a, b -> a.geometry.mbr.x1.compareTo(b.geometry.mbr.x1) }
    ),
    X_UPPER(
      Comparator<HasGeometry> { a, b -> a.geometry.mbr.x2.compareTo(b.geometry.mbr.x2) }
    ),
    Y_LOWER(
      Comparator<HasGeometry> { a, b -> a.geometry.mbr.y1.compareTo(b.geometry.mbr.y1) }
    ),
    Y_UPPER(
      Comparator<HasGeometry> { a, b -> a.geometry.mbr.y2.compareTo(b.geometry.mbr.y2) }
    )
  }

  internal fun <T : HasGeometry> pairsOf(minSize: Int, list: List<T>): List<GroupPair<T>> {
    val pairs = ArrayList<GroupPair<T>>(list.size - 2 * minSize + 1)
    for (i in minSize until list.size - minSize + 1) {
      // Note that subList returns a view of list so creating list1 and
      // list2 doesn't
      // necessarily incur array allocation costs.
      val list1 = list.subList(0, i)
      val list2 = list.subList(i, list.size)
      val pair = GroupPair.create(list1, list2)
      pairs.add(pair)
    }
    return pairs
  }

  private fun overlap(pair: GroupPair<out HasGeometry>): Double {
    return pair.first.geometry.mbr.intersectionArea(pair.second.geometry.mbr)
  }
}
