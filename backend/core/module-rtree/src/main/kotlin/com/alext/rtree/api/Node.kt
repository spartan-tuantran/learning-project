package com.alext.rtree.api

import com.alext.rtree.core.Context
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry

interface Node<T, S : Geometry> : HasGeometry {
  fun add(entry: Entry<T, S>): List<Node<T, S>>
  fun delete(entry: Entry<T, S>, all: Boolean): NodeEntry<T, S>
  fun count(): Int
  val context: Context
}
