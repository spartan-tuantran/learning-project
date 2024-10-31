package com.alext.rtree.core.splitter

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.core.geometry.HasGeometry
import com.alext.rtree.core.geometry.Rectangle

class QuadraticSplitter : Splitter {

  internal fun <T : HasGeometry> List<T>.bestCandidate(groupMbr: Rectangle): T {
    var minEntry: T? = null
    var minArea: Double? = null
    for (entry in this) {
      val area = groupMbr.merge(entry.geometry.mbr).area()
      if (minArea == null || area < minArea) {
        minArea = area
        minEntry = entry
      }
    }
    return minEntry!!
  }

  internal fun <T : HasGeometry> List<T>.worstCombination(): Pair<T, T> {
    var e1: T? = null
    var e2: T? = null
    var maxArea: Double? = null
    for (i in indices) {
      for (j in i + 1 until size) {
        val entry1 = this[i]
        val entry2 = this[j]
        val area = entry1.geometry.mbr.merge(entry2.geometry.mbr).area()
        if (maxArea == null || area > maxArea) {
          e1 = entry1
          e2 = entry2
          maxArea = area
        }
      }
    }
    return if (e1 != null && e2 != null) {
      Pair(e1, e2)
    } else {
      // all items are the same item
      Pair(this[0], this[1])
    }
  }

  /**
   * According to http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node
   * find the worst combination pairwise in the list and use them to start
   * the two groups
   */
  override fun <T : HasGeometry> split(items: List<T>, minSize: Int): GroupPair<T> {
    val worstCombination = items.worstCombination()
    // Worst combination to have in the same node is now e1,e2.
    // establish a group around e1 and another group around e2
    val group1 = mutableListOf(worstCombination.first)
    val group2 = mutableListOf(worstCombination.second)
    val remaining = ArrayList(items)
    remaining.remove(worstCombination.first)
    remaining.remove(worstCombination.second)
    val minGroupSize = items.size / 2

    // Now add the remainder to the groups using least mbr area increase
    // except in the case where minimumSize would be contradicted
    while (remaining.size > 0) {
      remaining.assign(group1, group2, minGroupSize)
    }
    return GroupPair.create(group1, group2)
  }

  private fun <T : HasGeometry> MutableList<T>.assign(group1: MutableList<T>, group2: MutableList<T>, minGroupSize: Int) {
    val mbr1 = Geometries.mbr(group1)
    val mbr2 = Geometries.mbr(group2)
    val item1 = bestCandidate(mbr1)
    val item2 = bestCandidate(mbr2)
    val area1LessThanArea2 = item1.geometry.mbr.merge(mbr1).area() <= item2.geometry.mbr.merge(mbr2).area()
    if (area1LessThanArea2 && group2.size + size - 1 >= minGroupSize || !area1LessThanArea2 && group1.size + size == minGroupSize) {
      group1.add(item1)
      remove(item1)
    } else {
      group2.add(item2)
      remove(item2)
    }
  }
}
