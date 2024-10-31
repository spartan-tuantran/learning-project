package com.alext.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

class JtsGeometryModule : SimpleModule() {
  init {
    addSerializer(Point::class.java, JtsPointSerializer())
    addDeserializer(Point::class.java, JtsPointDeserializer())
    addSerializer(LinearRing::class.java, JtsLinearRingSerializer())
    addDeserializer(LinearRing::class.java, JtsLinearRingDeserializer())
    addSerializer(Polygon::class.java, JtsPolygonSerializer())
    addDeserializer(Polygon::class.java, JtsPolygonDeserializer())
    addSerializer(MultiPolygon::class.java, JtsMultiPolygonSerializer())
    addDeserializer(MultiPolygon::class.java, JtsMultiPolygonDeserializer())
    addSerializer(Geometry::class.java, JtsGeometrySerializer())
    addDeserializer(Geometry::class.java, JtsGeometryDeserializer())
  }
}
