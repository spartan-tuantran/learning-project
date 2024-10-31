@file:Suppress("UNCHECKED_CAST")

package com.alext.rtree.misc.jts

import com.alext.rtree.misc.Earth
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.operation.buffer.BufferOp

typealias PostgisPolygon = org.postgis.Polygon
typealias PostgisMultiPolygon = org.postgis.MultiPolygon
typealias PostgisLineString = org.postgis.LineString
typealias PostgisLinearRing = org.postgis.LinearRing
typealias PostgisMultiLineString = org.postgis.MultiLineString
typealias PostgisPoint = org.postgis.Point
typealias PostgisMultiPoint = org.postgis.MultiPoint
typealias PostgisGeometry = org.postgis.Geometry

/**
 * Convert a [PostgisGeometry] to [org.locationtech.jts.geom.Geometry]
 * using well-known-text format
 *
 * Note: This method use [org.postgis.Polygon.outerWKT] when converting to geometry.
 */
inline fun <reified T : Geometry> PostgisGeometry.toJtsGeometry(bufferSize: Int = 1024): T {
  val buffer = StringBuffer(bufferSize).also { outerWKT(it) }
  return geometryFromWkt(buffer.toString()) as T
}

fun PostgisGeometry.toJtsGeometry(): Geometry {
  return toJtsGeometry<Geometry>()
}

/**
 * Convert a [org.locationtech.jts.geom.Geometry] to a [PostgisGeometry]
 * using well-known-text format.
 */
inline fun <reified T : PostgisGeometry> Geometry.toPostgisGeometry(): T {
  return GeometryFactory.from(geometryType).create(toText()) as T
}

fun PostgisPoint.toJstPoint(): Point {
  return org.locationtech.jts.geom.GeometryFactory(PrecisionModel(), srid).createPoint(Coordinate(x, y, z))
}

/**
 * Approximate a point on spherical map given a distance in meters and degree.
 *
 * Reference from
 * http://www.movable-type.co.uk/scripts/latlong.html
 *
 * @param distance The distance in meters
 * @param degree The degree to approximate. Note this is in degree not radian so the range is [0, 360]
 */
fun PostgisPoint.approximate(distance: Double, degree: Double): PostgisPoint {
  // Angular distance in radians
  val adr = distance / Earth.RADIUS_METERS
  val lat = y
  val lng = x
  val latRadians = Math.toRadians(lat)
  val lngRadians = Math.toRadians(lng)
  val degreeRadians = Math.toRadians(degree)
  val sphericalLat = asin(sin(latRadians) * cos(adr) + cos(latRadians) * sin(adr) * cos(degreeRadians))
  val theta = atan2(sin(degreeRadians) * sin(adr) * cos(latRadians), cos(adr) - sin(latRadians) * sin(sphericalLat))
  val sphericalLng = mod(lngRadians + theta + PI, 2 * PI) - PI
  return PostgisPoint(Math.toDegrees(sphericalLng), Math.toDegrees(sphericalLat))
}

/**
 * Create a circle from a center and radius in meters
 *
 * @param radius The radius in meters
 * @param diagonal The flag to indicate we want to use diagonal as length
 */
fun PostgisPoint.circle(radius: Double, diagonal: Boolean = true): Polygon {
  val distance = distance(radius, diagonal)
  return toJstPoint().buffer(distance) as Polygon
}

/**
 * Extends the polygon to a radius in meters using JTS buffer method. Note that
 * the use of [radius] as parameter is intended to conform to other methods and make it clear
 * the unit is in meters.
 *
 * The buffering algorithm basically extends all edges of a polygon using [BufferOp.CAP_FLAT] with 0 quadrant segments
 * minimize the number of points in extended polygons.
 *
 *           ########
 * ###### -> ########
 * #    #    ##    ##
 * ######    ########
 *           ########
 * @param radius The distance in meters
 */
fun PostgisPolygon.extend(radius: Double): PostgisPolygon {
  val centroid = centroid()
  val point = PostgisPoint(centroid.x, centroid.y)
  return buffer(point.distance(radius, diagonal = true), 0, BufferOp.CAP_FLAT) as PostgisPolygon
}

/**
 * Create a rectangle bounding box from a given distance in meters
 *
 *  (l, t)    (r, t)
 *    ##########
 *    #        #
 *    #        #
 *    #        #
 *    ##########
 *  (l, b)     (r, b)
 *
 * @param radius The radius in meters
 */
fun PostgisPoint.rectangle(radius: Double): Polygon {
  val north = approximate(radius, 0.0)
  val south = approximate(radius, 180.0)
  val east = approximate(radius, 90.0)
  val west = approximate(radius, 270.0)
  val left = west.x
  val top = south.y
  val right = east.x
  val bottom = north.y
  val ring = PostgisLinearRing(
    arrayOf(
      PostgisPoint(left, top),
      PostgisPoint(right, top),
      PostgisPoint(right, bottom),
      PostgisPoint(left, bottom),
      PostgisPoint(left, top)
    )
  )
  return PostgisPolygon(arrayOf(ring)).toJtsGeometry<Polygon>()
}

/**
 * Compute the relative distance to radius in meters from a center point
 *
 * @param radius The radius in meters
 * @param diagonal True if we want to use diagonal as length otherwise
 */
fun PostgisPoint.distance(radius: Double, diagonal: Boolean): Double {
  val north = approximate(radius, 0.0)
  val south = approximate(radius, 180.0)
  val east = approximate(radius, 90.0)
  val west = approximate(radius, 270.0)
  return if (diagonal) {
    distance(west.x, south.y, east.x, north.y) / 2.0
  } else {
    max(
      distance(north.x, north.y, south.x, south.y),
      distance(east.x, east.y, west.x, west.y)
    ) / 2.0
  }
}

/**
 * Standard euclidean distance between 2 points
 */
fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
  val dx = x2 - x1
  val dy = y2 - y1
  return sqrt(dx * dx + dy * dy)
}

/**
 * Modulo for double n % m
 *
 * @param n The number to modulo
 * @param m The modulo value
 */
internal fun mod(n: Double, m: Double): Double {
  val a = abs(m)
  val r = (n / a).toInt()
  var result = n - a * r
  if (result < 0) {
    result += a
  }
  return result
}
