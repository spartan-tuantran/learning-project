package com.alext.rtree.extension.jts

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import org.locationtech.jts.geom.MultiPolygon

interface TaggableGeoMultiPolygon<T> : Geometry {
  val polygon: MultiPolygon
  val tag: T?
  fun intersects(geometry: org.locationtech.jts.geom.Geometry): Boolean
}

fun <T> Geometries.taggableGeoMultiPolygon(polygon: MultiPolygon, tag: T? = null): TaggableGeoMultiPolygon<T> {
  return object : TaggableGeoMultiPolygon<T> {
    override val polygon: MultiPolygon = polygon
    override val tag: T? = tag
    override val mbr: Rectangle = mbr(polygon)

    override fun intersects(geometry: org.locationtech.jts.geom.Geometry): Boolean {
      return this.polygon.intersects(geometry)
    }

    override fun distance(rectangle: Rectangle): Double {
      return mbr.distance(rectangle)
    }

    override fun intersects(rectangle: Rectangle): Boolean {
      return mbr.intersects(rectangle)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is TaggableGeoMultiPolygon<*>) return false
      return polygon.equals(other.polygon) && tag == other.tag
    }

    override fun hashCode(): Int {
      return polygon.hashCode()
    }

    override fun toString(): String {
      return polygon.toString()
    }
  }
}
