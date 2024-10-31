package com.alext.rtree.misc.jts

import kotlin.reflect.KClass
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

enum class GeometryFactory(
  val jst: KClass<out Geometry>,
  val postgis: KClass<out PostgisGeometry>
) {

  POINT(Point::class, PostgisPoint::class) {
    override fun child(): GeometryFactory? {
      return null
    }

    override fun create(wkt: String): PostgisGeometry {
      return PostgisPoint(wkt)
    }
  },

  MULTI_POINT(MultiPoint::class, PostgisMultiPoint::class) {
    override fun child(): GeometryFactory? {
      return POINT
    }

    override fun create(wkt: String): PostgisGeometry {
      return PostgisMultiPoint(wkt)
    }
  },

  LINE_STRING(LineString::class, PostgisLineString::class) {
    override fun child(): GeometryFactory? {
      return MULTI_POINT
    }

    override fun create(wkt: String): PostgisGeometry {
      return PostgisLineString(wkt)
    }
  },

  MULTI_LINE_STRING(MultiLineString::class, PostgisMultiLineString::class) {
    override fun child(): GeometryFactory? {
      return LINE_STRING
    }

    override fun create(wkt: String): PostgisGeometry {
      return PostgisMultiLineString(wkt)
    }
  },

  POLYGON(Polygon::class, PostgisPolygon::class) {
    override fun child(): GeometryFactory? {
      return MULTI_LINE_STRING
    }

    override fun create(wkt: String): PostgisGeometry {
      return PostgisPolygon(wkt)
    }
  },

  MULTI_POLYGON(MultiPolygon::class, PostgisMultiPolygon::class) {
    override fun child(): GeometryFactory? {
      return POLYGON
    }
    override fun create(wkt: String): PostgisGeometry {
      return PostgisMultiPolygon(wkt)
    }
  },

  GEOMETRY(Geometry::class, PostgisGeometry::class) {
    override fun child(): GeometryFactory? {
      return null
    }

    override fun create(wkt: String): PostgisGeometry {
      throw UnsupportedOperationException("Generic geometry type is not supported.")
    }
  };

  /**
   * Create a postgis geometry from WKT text
   *
   * @param wkt WKT text for geometry. Must be valid
   */
  abstract fun create(wkt: String): PostgisGeometry

  /**
   * Direct child factory of this factory, null means it's a leaf
   */
  abstract fun child(): GeometryFactory?

  companion object {
    private val ALL = values()

    fun from(type: String): GeometryFactory {
      return ALL.firstOrNull { it.jst.simpleName == type } ?: GEOMETRY
    }
  }
}
