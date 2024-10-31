package com.alext.rtree.extension.jts

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Rectangle
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon

/**
 * Compute the minimum bounding rectangle for a [Polygon]
 *
 * @param polygon The polygon to be computed
 */
fun Geometries.mbr(polygon: Polygon): Rectangle {
  return mbr(polygon.coordinates)
}

/**
 * Compute the minimum bounding rectangle for a [MultiPolygon]
 *
 * @param polygon The multiple polygon to be computed
 */
fun Geometries.mbr(polygon: MultiPolygon): Rectangle {
  return mbr(polygon.coordinates)
}

fun Geometries.mbr(coordinates: Array<Coordinate>): Rectangle {
  var minX = coordinates[0].x
  var minY = coordinates[0].y
  var maxX = coordinates[0].x
  var maxY = coordinates[0].y
  val size = coordinates.size
  for (i in 1 until size) {
    val c = coordinates[i]
    if (c.x < minX) {
      minX = c.x
    }
    if (c.y < minY) {
      minY = c.y
    }
    if (c.x > maxX) {
      maxX = c.x
    }
    if (c.y > maxY) {
      maxY = c.y
    }
  }
  return rectangle(minX, minY, maxX, maxY)
}

/**
 * Compute the minimum bounding rectangle for a list of polygon
 *
 * @param polygons A list of polygons
 */
fun Geometries.mbr(polygons: List<Polygon>): Rectangle {
  return mbr(polygons.map { mbr(it) })
}
