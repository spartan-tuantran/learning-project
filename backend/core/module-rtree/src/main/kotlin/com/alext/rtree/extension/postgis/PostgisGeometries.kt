package com.alext.rtree.extension.postgis

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.misc.jts.approximate

typealias PostgisPolygon = org.postgis.Polygon
typealias PostgisPoint = org.postgis.Point

fun PostgisPoint.mbr(radius: Double): Rectangle {
  val north = approximate(radius, 0.0)
  val south = approximate(radius, 180.0)
  val east = approximate(radius, 90.0)
  val west = approximate(radius, 270.0)
  return Geometries.rectangle(west.x, south.y, east.x, north.y)
}
