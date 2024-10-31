package com.alext.database.utility

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.io.WKTReader

object Geometries {

  private val FACTORY = org.locationtech.jts.geom.GeometryFactory(PrecisionModel(PrecisionModel.FLOATING), 4326)
  val READER = WKTReader(FACTORY)

  inline fun <reified G : Geometry> create(wkt: String): G {
    val geometry = READER.read(wkt) as G
    geometry.srid = 4326
    return geometry
  }

  fun point(latitude: Double, longitude: Double): Point {
    return FACTORY.createPoint(Coordinate(longitude, latitude))
  }
}
