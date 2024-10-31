package com.alext.rtree.core.geometry

import com.alext.rtree.document.NotThreadSafe

@NotThreadSafe
class GroupPair<T : HasGeometry> private constructor(
  val first: Group<T>,
  val second: Group<T>
) {

  val marginSum: Double by lazy(LazyThreadSafetyMode.NONE) {
    first.geometry.mbr.perimeter() + second.geometry.mbr.perimeter()
  }

  val areaSum: Double by lazy(LazyThreadSafetyMode.NONE) {
    first.geometry.mbr.area() + second.geometry.mbr.area()
  }

  companion object Factory {

    fun <T : HasGeometry> create(list1: List<T>, list2: List<T>): GroupPair<T> {
      return GroupPair(
        first = Group(list1, Geometries.mbr(list1)),
        second = Group(list2, Geometries.mbr(list2))
      )
    }
  }
}
