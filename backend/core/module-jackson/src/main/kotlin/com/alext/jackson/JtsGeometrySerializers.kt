package com.alext.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.PrecisionModel

private const val RINGS = "rings"
private const val POINTS = "points"
private const val POLYGONS = "polygons"
private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"
private const val WGS84: Int = 4326

private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), WGS84)

class JtsGeometrySerializer : JsonSerializer<Geometry>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Geometry, gen: JsonGenerator, serializers: SerializerProvider) {
    when (value) {
      is Point -> {
        JtsPointSerializer().serialize(value, gen, serializers)
      }
      is LinearRing -> {
        JtsLinearRingSerializer().serialize(value, gen, serializers)
      }
      is Polygon -> {
        JtsPolygonSerializer().serialize(value, gen, serializers)
      }
      is MultiPolygon -> {
        JtsMultiPolygonSerializer().serialize(value, gen, serializers)
      }
    }
  }
}

class JtsGeometryDeserializer : JsonDeserializer<Geometry>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Geometry? {
    val node = p.readNode()
    return node.toPolygon() ?: node.toMultiPolygon() ?: node.toLinearRing() ?: node.point()
  }
}

class JtsPointSerializer : JsonSerializer<Point>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Point, gen: JsonGenerator, serializers: SerializerProvider) {
    with(gen) {
      writeStartArray()
      writeNumber(value.x) // Longitude is x
      writeNumber(value.y) // Latitude is y
      writeEndArray()
    }
  }
}

class JtsPointDeserializer : JsonDeserializer<Point>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Point? {
    return p.readNode().point()
  }
}

class JtsLinearRingSerializer : JsonSerializer<LinearRing>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: LinearRing, gen: JsonGenerator, serializers: SerializerProvider) {
    with(gen) {
      writeStartObject()
      writeObjectField(POINTS, value.points())
      writeEndObject()
    }
  }
}

class JtsLinearRingDeserializer : JsonDeserializer<LinearRing>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LinearRing? {
    return p.readNode().toLinearRing()
  }
}

class JtsPolygonSerializer : JsonSerializer<Polygon>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Polygon, gen: JsonGenerator, serializers: SerializerProvider) {
    with(gen) {
      writeStartObject()
      writeObjectField(RINGS, value.rings())
      writeEndObject()
    }
  }
}

class JtsPolygonDeserializer : JsonDeserializer<Polygon>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Polygon? {
    return p.readNode().toPolygon()
  }
}

class JtsMultiPolygonSerializer : JsonSerializer<MultiPolygon>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: MultiPolygon, gen: JsonGenerator, serializers: SerializerProvider) {
    with(gen) {
      writeStartObject()
      writeObjectField(POLYGONS, value.polygons())
      writeEndObject()
    }
  }
}

class JtsMultiPolygonDeserializer : JsonDeserializer<MultiPolygon>() {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MultiPolygon? {
    return p.readNode().toMultiPolygon()
  }
}

private fun JsonNode.toLinearRing(): LinearRing? {
  val array = get(POINTS) ?: return null
  val coordinates = Array(array.size()) { i ->
    array.get(i)?.point()?.coordinate
  }
  return GEOMETRY_FACTORY.createLinearRing(coordinates)
}

private fun JsonNode.toPolygon(): Polygon? {
  return get(RINGS)?.let { rings ->
    val coordinates = mutableListOf<Coordinate>()
    rings.forEach { node ->
      node.toLinearRing()?.coordinates?.forEach { c ->
        coordinates.add(c)
      }
    }
    GEOMETRY_FACTORY.createPolygon(coordinates.toTypedArray())
  }
}

private fun JsonNode.toMultiPolygon(): MultiPolygon? {
  return get(POLYGONS)?.let {
    val polygons = Array(it.size()) { i ->
      it[i].toPolygon()
    }
    GEOMETRY_FACTORY.createMultiPolygon(polygons)
  }
}

/**
 * Convert a [JsonNode] to point but consider both format in order to fallback to old
 * serializer. Once all services are fully released, we can just remove the check for array
 * 1) {"latitude": 34.0, "longitude": -118.0}
 * 2) [34.0, -118.0]
 */
private fun JsonNode.point(): Point? {
  return if (isArray) {
    jtsPoint(
      x = get(0)?.asDouble(),
      y = get(1)?.asDouble()
    )
  } else {
    jtsPoint(
      x = get(LONGITUDE)?.asDouble(),
      y = get(LATITUDE)?.asDouble()
    )
  }
}

private fun jtsPoint(x: Double?, y: Double?): Point? {
  return if (x != null && y != null) {
    GEOMETRY_FACTORY.createPoint(Coordinate(x, y))
  } else {
    null
  }
}

private fun LinearRing.points(): List<Point> {
  return coordinates.map {
    GEOMETRY_FACTORY.createPoint(it)
  }
}

private fun Polygon.rings(): List<LinearRing> {
  val size = numInteriorRing + if (exteriorRing == null) 0 else 1
  val rings = ArrayList<LinearRing>(size)
  exteriorRing?.let { rings.add(exteriorRing.ring()) }
  for (i in 0 until numInteriorRing) {
    rings.add(getInteriorRingN(i).ring())
  }
  return rings
}

private fun MultiPolygon.polygons(): List<Polygon> {
  return List(
    numGeometries,
    init = { i ->
      getGeometryN(i) as Polygon
    }
  )
}

private fun LineString.ring(): LinearRing {
  return GEOMETRY_FACTORY.createLinearRing(coordinates)
}
