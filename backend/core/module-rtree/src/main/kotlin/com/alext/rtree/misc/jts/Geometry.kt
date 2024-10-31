package com.alext.rtree.misc.jts

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.io.WKTReader

/**
 * Turn a geometry collection into a list of sub geometries
 */
fun GeometryCollection.asSequence(): Sequence<Geometry> {
  return object : Sequence<Geometry> {
    override fun iterator(): Iterator<Geometry> {
      var i = 0
      return object : Iterator<Geometry> {
        override fun hasNext() = i < numGeometries
        override fun next() = getGeometryN(i++)
      }
    }
  }
}

/**
 * Return all sub-geometries for this geometry collection
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : GeometryCollection, reified V : Geometry> T.children(): List<V> {
  return asSequence().toList() as List<V>
}

/**
 * Construct a class that extends [Geometry] object from WKT text
 *
 * @param wkt Well-known text geometry
 */
inline fun <reified T : Geometry> geometryFromWkt(wkt: String): T {
  return WKTReader().read(wkt) as T
}

/**
 * Split the collection of geometry into two parts:
 * 1. The collection of geometry from index [1, n - 1]
 * 2. The geometry at index 0
 * Primary used for operation such as: union, intersections..
 */
internal fun Collection<Geometry>.split(): Pair<GeometryCollection, Geometry> {
  val collection = first().factory.createGeometryCollection(drop(1).toTypedArray())
  return Pair(collection, first())
}
