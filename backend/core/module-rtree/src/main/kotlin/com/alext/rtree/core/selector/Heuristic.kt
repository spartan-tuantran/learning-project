package com.alext.rtree.core.selector

import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry
import com.alext.rtree.core.geometry.Rectangle

/**
 * Return the area of a [Rectangle] and [Geometry]
 *
 * @param rectangle The rectangle
 * @param geometry The geometry
 */
internal fun area(rectangle: Rectangle, geometry: HasGeometry): Double {
  return geometry.geometry.mbr.merge(rectangle).area()
}

/**
 * Return the overlapping area of a [Rectangle] a list of [Geometry] and the current geometry
 *
 * @param rectangle The rectangle
 * @param others The list of geometries
 * @param geometry The target geometry
 */
internal fun areaOverlap(rectangle: Rectangle, others: List<HasGeometry>, geometry: HasGeometry): Double {
  val added = geometry.geometry.mbr.merge(rectangle)
  var area = 0.0
  for (other in others) {
    if (other !== geometry) {
      area += added.intersectionArea(other.geometry.mbr)
    }
  }
  return area
}

/**
 * Return the area difference between a [Rectangle] and a geometry
 *
 * @param rectangle The rectangle
 * @param geometry The geometry
 */
internal fun areaIncrease(rectangle: Rectangle, geometry: HasGeometry): Double {
  val added = geometry.geometry.mbr.merge(rectangle)
  return added.area() - geometry.geometry.mbr.area()
}
