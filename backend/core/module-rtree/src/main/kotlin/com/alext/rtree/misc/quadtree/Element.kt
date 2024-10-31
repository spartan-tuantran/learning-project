package com.alext.rtree.misc.quadtree

data class Element<T>(
  val point: Point = Point(0.0, 0.0),
  val value: T
)
