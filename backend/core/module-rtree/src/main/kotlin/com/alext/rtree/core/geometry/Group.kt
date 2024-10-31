package com.alext.rtree.core.geometry

/**
 * A geometry group that contains the primary [Geometry] and a
 * list of items that implement [HasGeometry]
 */
class Group<T : HasGeometry>(
  val items: List<T>,
  override val geometry: Geometry
) : HasGeometry
