package com.alext.rtree.misc.jts

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.math.Maths
import com.alext.rtree.misc.Earth
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

fun Point.mbr(radius: Double): Rectangle {
  val north = approximate(radius, 0.0)
  val south = approximate(radius, 180.0)
  val east = approximate(radius, 90.0)
  val west = approximate(radius, 270.0)
  return Geometries.rectangle(west.x, south.y, east.x, north.y)
}

fun Point.approximate(distance: Double, degree: Double): Point {
  // Angular distance in radians
  val adr = distance / Earth.RADIUS_METERS
  val lat = y
  val lng = x
  val latRadians = Math.toRadians(lat)
  val lngRadians = Math.toRadians(lng)
  val degreeRadians = Math.toRadians(degree)
  val sphericalLat = asin(sin(latRadians) * cos(adr) + cos(latRadians) * sin(adr) * cos(degreeRadians))
  val theta = atan2(sin(degreeRadians) * sin(adr) * cos(latRadians), cos(adr) - sin(latRadians) * sin(sphericalLat))
  val sphericalLng = Maths.mod(lngRadians + theta + kotlin.math.PI, 2 * kotlin.math.PI) - kotlin.math.PI
  return JtsGeometries.FACTORY.createPoint(Coordinate(Math.toDegrees(sphericalLng), Math.toDegrees(sphericalLat)))
}

fun Point.circle(radius: Double, diagonal: Boolean = true): Polygon {
  val distance = distance(radius, diagonal)
  return buffer(distance) as Polygon
}

fun Point.distance(radius: Double, diagonal: Boolean): Double {
  val north = approximate(radius, 0.0)
  val south = approximate(radius, 180.0)
  val east = approximate(radius, 90.0)
  val west = approximate(radius, 270.0)
  return if (diagonal) {
    Maths.distance(west.x, south.y, east.x, north.y) / 2.0
  } else {
    max(
      Maths.distance(north.x, north.y, south.x, south.y),
      Maths.distance(east.x, east.y, west.x, west.y)
    ) / 2.0
  }
}
