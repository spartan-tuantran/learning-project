package com.alext.rtree.misc.postgis

import org.postgis.Geometry
import org.postgis.LinearRing
import org.postgis.Point
import org.postgis.Polygon

fun Geometry.circleOfPointsToPolygon(): Polygon {
  val points = ArrayList<Point>(this.numPoints())
  (0 until this.numPoints()).map {
    points.add(this.getPoint(it))
  }
  return Polygon(listOf(LinearRing(points.toTypedArray())).toTypedArray())
}
