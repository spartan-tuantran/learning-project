package com.alext.rtree.extension.jts

import com.alext.rtree.algorithm.jts.RayCastingPolygonIntersectionAlgorithm
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

interface TaggableGeoPoint<T> : Geometry {
  val point: Point
  val tag: T?
  fun intersects(geometry: org.locationtech.jts.geom.Geometry): Boolean
}

fun <T> Geometries.taggableGeoPoint(point: Point, tag: T? = null): TaggableGeoPoint<T> {
  return object : TaggableGeoPoint<T>, RayCastingPolygonIntersectionAlgorithm {
    override val point: Point = point
    override val tag: T? = tag
    override val mbr: Rectangle = point(point.x, point.y)

    override fun intersects(geometry: org.locationtech.jts.geom.Geometry): Boolean {
      return if (geometry is Polygon) {
        geometry.intersectsWith(point)
      } else {
        point.intersects(geometry)
      }
    }

    override fun distance(rectangle: Rectangle): Double {
      return rectangle.distance(mbr)
    }

    override fun intersects(rectangle: Rectangle): Boolean {
      return mbr.intersects(rectangle)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is TaggableGeoPoint<*>) return false
      if (point.x != other.point.x) return false
      if (point.y != other.point.y) return false
      if (tag != other.tag) return false
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
