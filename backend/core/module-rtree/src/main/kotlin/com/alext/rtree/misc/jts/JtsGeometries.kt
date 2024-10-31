package com.alext.rtree.misc.jts

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.util.GeometricShapeFactory

object JtsGeometries {

  private const val CIRCLE_MAX_POINTS = 32
  val FACTORY = org.locationtech.jts.geom.GeometryFactory(PrecisionModel(PrecisionModel.FLOATING), 4326)
  val READER = WKTReader(FACTORY)

  inline fun <reified G : Geometry> create(wkt: String): G {
    return READER.read(wkt) as G
  }

  fun point(latitude: Double, longitude: Double): Point {
    return FACTORY.createPoint(Coordinate(longitude, latitude))
  }

  fun circle(center: Point, radius: Double): Geometry {
    val factory = GeometricShapeFactory(FACTORY).apply {
      setNumPoints(CIRCLE_MAX_POINTS)
      setCentre(Coordinate(center.x, center.y))
      setSize(radius * 2)
    }
    return factory.createCircle()
  }
}
