package com.alext.rtree.misc.jts

import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.operation.buffer.BufferOp

/**
 * Extends the polygon to a radius in meters using JTS buffer method. Note that
 * the use of [radius] as parameter is intended to conform to other methods and make it clear
 * the unit is in meters.
 *
 * The buffering algorithm basically extends all edges of a polygon using [BufferOp.CAP_FLAT] with 0 quadrant segments
 * minimize the number of points in extended polygons.
 *
 *           ########
 * ###### -> ########
 * #    #    ##    ##
 * ######    ########
 *           ########
 * @param radius The distance in meters
 */
fun Polygon.extend(radius: Double): Polygon {
  val point = PostgisPoint(centroid.x, centroid.y)
  return buffer(point.distance(radius, diagonal = true), 0, BufferOp.CAP_FLAT) as Polygon
}
