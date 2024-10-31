package com.alext.rtree.misc.jts

import org.locationtech.jts.geom.Geometry

/**
 * Return the union of all polygons using [Geometry.buffer(0.0)]
 *
 * @param polygons A list of polygons
 */
internal fun union(polygons: List<Geometry>): Geometry {
  require(polygons.isNotEmpty()) {
    "Attempt to find union of an empty polygon list!"
  }
  val collection = polygons.first().factory.createGeometryCollection(polygons.toTypedArray())
  return collection.buffer(0.0)
}

fun union(vararg polygon: PostgisGeometry): PostgisGeometry {
  return union(polygon.map { it.toJtsGeometry() }).toPostgisGeometry()
}

fun union(polygon: List<PostgisGeometry>): PostgisGeometry {
  return union(polygon.map { it.toJtsGeometry() }).toPostgisGeometry()
}

fun PostgisGeometry.union(other: PostgisGeometry): PostgisGeometry {
  return toJtsGeometry().union(other.toJtsGeometry()).toPostgisGeometry()
}

fun PostgisGeometry.difference(other: PostgisGeometry): PostgisGeometry {
  return toJtsGeometry().difference(other.toJtsGeometry()).toPostgisGeometry()
}

fun PostgisGeometry.intersection(other: PostgisGeometry): PostgisGeometry {
  return toJtsGeometry().intersection(other.toJtsGeometry()).toPostgisGeometry()
}

fun PostgisGeometry.toGeometry(): PostgisGeometry {
  return toJtsGeometry().toPostgisGeometry()
}
