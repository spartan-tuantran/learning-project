package com.alext.rtree.extension.jts

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import org.locationtech.jts.geom.Point

interface GeoPoint : Geometry {
  val point: Point
}

fun Geometries.geoPoint(point: Point): GeoPoint {
  return object : GeoPoint {
    override val point: Point = point
    override val mbr: Rectangle = point(point.x, point.y)

    override fun distance(rectangle: Rectangle): Double {
      return rectangle.distance(mbr)
    }

    override fun intersects(rectangle: Rectangle): Boolean {
      return mbr.intersects(rectangle)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is GeoPoint) return false
      if (point.x != other.point.x) return false
      if (point.y != other.point.y) return false
      return true
    }

    override fun hashCode(): Int {
      return point.hashCode()
    }

    override fun toString(): String {
      return point.toString()
    }
  }
}
