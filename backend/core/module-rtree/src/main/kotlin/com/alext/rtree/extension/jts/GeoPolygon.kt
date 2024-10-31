package com.alext.rtree.extension.jts

import com.alext.rtree.algorithm.jts.RayCastingPolygonIntersectionAlgorithm
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

interface GeoPolygon : Geometry {
  val polygon: Polygon
  fun intersects(geometry: org.locationtech.jts.geom.Geometry): Boolean
}

fun Geometries.geoPolygon(polygon: Polygon): GeoPolygon {
  return object : GeoPolygon, RayCastingPolygonIntersectionAlgorithm {

    override val polygon: Polygon = polygon
    override val mbr: Rectangle = mbr(polygon)

    override fun intersects(geometry: org.locationtech.jts.geom.Geometry): Boolean {
      return if (geometry is Point) {
        polygon.intersectsWith(geometry)
      } else {
        polygon.intersects(geometry)
      }
    }

    override fun distance(rectangle: Rectangle): Double {
      return mbr.distance(rectangle)
    }

    override fun intersects(rectangle: Rectangle): Boolean {
      return mbr.intersects(rectangle)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is GeoPolygon) return false
      return polygon.equals(other.polygon)
    }

    override fun hashCode(): Int {
      return polygon.hashCode()
    }

    override fun toString(): String {
      return polygon.toString()
    }
  }
}
