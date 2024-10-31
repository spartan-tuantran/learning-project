package com.alext.rtree.misc.postgis

import kotlin.math.cos
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.util.GeometricShapeFactory
import org.postgis.LinearRing
import org.postgis.Point
import org.postgis.Polygon

const val METERS_PER_DEGREE_LATITUDE = 111_320.0
const val METERS_PER_DEGREE_LONGITUDE_AT_EQUATOR = 111_319.4
const val CIRCULAR_GEOMETRY_POINT_COUNT = 32

/**
 * Creates a circular Polygon with radius [radius] centered around [this], estimated using [numPoints] points.
 *
 * This function uses a Euclidean geodesic approximation which is highly accurate for relatively small radii.
 */
fun Point.createCirclePolygonAround(
  radius: Double,
  numPoints: Int = CIRCULAR_GEOMETRY_POINT_COUNT
): Polygon {
  val points = if (radius <= 0.0 || numPoints <= 1) {
    arrayOf(Point(x, y))
  } else {
    val diameter = radius * 2.0
    val polygon = with(GeometricShapeFactory()) {
      setNumPoints(numPoints.coerceAtLeast(1))
      setCentre(Coordinate(x, y))
      setHeight(diameter / METERS_PER_DEGREE_LATITUDE)
      setWidth(diameter / (METERS_PER_DEGREE_LONGITUDE_AT_EQUATOR * cos(Math.toRadians(y))))
      createEllipse()
    }
    polygon.exteriorRing.coordinates.map { Point(it.x, it.y) }.toTypedArray()
  }

  return Polygon(arrayOf(LinearRing(points)))
}
