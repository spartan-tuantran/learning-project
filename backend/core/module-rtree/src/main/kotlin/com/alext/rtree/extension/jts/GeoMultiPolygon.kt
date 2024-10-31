package com.alext.rtree.extension.jts

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import org.locationtech.jts.geom.MultiPolygon

interface GeoMultiPolygon : Geometry {
  val polygon: MultiPolygon
}

fun Geometries.geoMultiPolygon(polygon: MultiPolygon): GeoMultiPolygon {
  return object : GeoMultiPolygon {

    override val polygon: MultiPolygon = polygon

    override val mbr: Rectangle = mbr(polygon)

    override fun distance(rectangle: Rectangle): Double {
      return mbr.distance(rectangle)
    }

    override fun intersects(rectangle: Rectangle): Boolean {
      return mbr.intersects(rectangle)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is GeoMultiPolygon) return false
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
