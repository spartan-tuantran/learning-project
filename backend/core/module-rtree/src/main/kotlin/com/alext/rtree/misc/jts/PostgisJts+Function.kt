package com.alext.rtree.misc.jts

import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.TopologyException
import org.locationtech.jts.operation.buffer.BufferOp
import org.postgis.Geometry

fun PostgisGeometry.reverse(): PostgisGeometry {
  return toJtsGeometry().reverse().toPostgisGeometry()
}

fun PostgisGeometry.convexHull(): PostgisGeometry {
  return toJtsGeometry().convexHull().toPostgisGeometry()
}

fun PostgisGeometry.simple(): Boolean {
  return toJtsGeometry().isSimple
}

fun PostgisGeometry.valid(): Boolean {
  return toJtsGeometry().isValid
}

fun PostgisGeometry.rectangle(): Boolean {
  return toJtsGeometry().isRectangle
}

fun PostgisGeometry.empty(): Boolean {
  return toJtsGeometry().isEmpty
}

fun PostgisGeometry.distance(other: PostgisGeometry): Double {
  return toJtsGeometry().distance(other.toJtsGeometry())
}

fun PostgisGeometry.area(): Double {
  return toJtsGeometry().area
}

fun PostgisGeometry.centroid(): Point {
  return toJtsGeometry().centroid
}

/**
 * Computes a buffer area around this geometry having the given width. The
 * buffer of a Geometry is the Minkowski sum or difference of the geometry
 * with a disc of radius <code>abs(distance)</code>.
 * <p>
 * Mathematically-exact buffer area boundaries can contain circular arcs.
 * To represent these arcs using linear geometry they must be approximated with line segments.
 * The buffer geometry is constructed using 8 segments per quadrant to approximate
 * the circular arcs.
 * The end cap style is <code>CAP_ROUND</code>.
 * <p>
 * The buffer operation always returns a polygonal result. The negative or
 * zero-distance buffer of lines and points is always an empty {@link Polygon}.
 * This is also the result for the buffers of degenerate (zero-area) polygons.
 *
 * @param distance
 *          the width of the buffer (may be positive, negative or 0)
 * @return a polygonal geometry representing the buffer region (which may be
 *         empty)
 *
 * @throws TopologyException
 *           if a robustness error occurs
 *
 * @see #buffer(double, int)
 * @see #buffer(double, int, int)
 */
fun PostgisGeometry.buffer(distance: Double): Geometry {
  return toJtsGeometry().buffer(distance).toPostgisGeometry()
}

/**
 * Computes a buffer area around this geometry having the given
 * width and with a specified accuracy of approximation for circular arcs,
 * and using a specified end cap style.
 * <p>
 * Mathematically-exact buffer area boundaries can contain circular arcs.
 * To represent these arcs using linear geometry they must be approximated with line segments.
 * The <code>quadrantSegments</code> argument allows controlling the
 * accuracy of the approximation
 * by specifying the number of line segments used to represent a quadrant of a circle
 * <p>
 * The end cap style specifies the buffer geometry that will be
 * created at the ends of linestrings.  The styles provided are:
 * <ul>
 * <li><code>BufferOp.CAP_ROUND</code> - (default) a semi-circle
 * <li><code>BufferOp.CAP_BUTT</code> - a straight line perpendicular to the end segment
 * <li><code>BufferOp.CAP_SQUARE</code> - a half-square
 * </ul>
 * <p>
 * The buffer operation always returns a polygonal result. The negative or
 * zero-distance buffer of lines and points is always an empty {@link Polygon}.
 * This is also the result for the buffers of degenerate (zero-area) polygons.
 *
 *@param distance the width of the buffer (may be positive, negative or 0)
 *@param quadrantSegments the number of line segments used to represent a quadrant of a circle
 *@param endCapStyle the end cap style to use
 *@return a polygonal geometry representing the buffer region (which may be empty)
 *
 * @throws TopologyException if a robustness error occurs
 *
 * @see #buffer(double)
 * @see #buffer(double, int)
 * @see BufferOp
 */
fun PostgisGeometry.buffer(distance: Double, quadrantSegments: Int, endCapStyle: Int): Geometry {
  return toJtsGeometry().buffer(distance, quadrantSegments, endCapStyle).toPostgisGeometry()
}

fun Double.metersToDecimalDegrees(): Double = this / 111_000

fun Double.decimalDegreesToMeters(): Double = this * 111_000
