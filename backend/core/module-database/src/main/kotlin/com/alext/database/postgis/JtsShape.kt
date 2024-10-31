/*
 * Binary Parser for JTS - relies on org.postgis V1.0.0+ package.
 *
 * (C) 2005 Markus Schaber, markus.schaber@logix-tt.com
 * (C) 2015 Phillip Ross, phillip.w.g.ross@gmail.com
 * (C) 2019 Chan Nguyen, atbl1511@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
@file:Suppress("unused")

package com.alext.database.postgis

import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.PathIterator
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.CoordinateSequence
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.impl.PackedCoordinateSequence

class JtsShape(
  private val geom: Geometry
) : Shape {

  constructor(geom: JtsPgGeometry) : this(geom.geometry)

  override fun contains(p: Point2D): Boolean {
    return contains(p.x, p.y)
  }

  override fun contains(x: Double, y: Double): Boolean {
    val c = Coordinate(x, y)
    val p = GEOMETRY_FACTORY.createPoint(c)
    return geom.contains(p)
  }

  override fun contains(r: Rectangle2D): Boolean {
    return contains(r.minX, r.minY, r.width, r.height)
  }

  override fun contains(x: Double, y: Double, w: Double, h: Double): Boolean {
    val r = createRect(x, y, w, h)
    return geom.contains(r)
  }

  private fun createRect(x: Double, y: Double, w: Double, h: Double): Polygon {
    val arr = doubleArrayOf(x, y, x + w, y, x + w, y + h, x, y + h, x, y)
    val shell = PackedCoordinateSequence.Double(arr, 2, 0)
    return GEOMETRY_FACTORY.createPolygon(GEOMETRY_FACTORY.createLinearRing(shell), NO_SHELLS)
  }

  override fun getBounds2D(): Rectangle2D {
    val env = geom.envelopeInternal
    return Rectangle2D.Double(env.minX, env.maxX, env.width, env.height)
  }

  override fun getBounds(): Rectangle {
    // We deal simple code for efficiency here, the getBounds() rounding
    // rules are ugly...
    return bounds2D.bounds
  }

  override fun getPathIterator(at: AffineTransform): PathIterator {
    return pathIterator(geom, at)
  }

  override fun getPathIterator(at: AffineTransform, flatness: Double): PathIterator {
    // we don't have much work here, as we only have linear segments, no
    // "flattening" necessary.
    return getPathIterator(at)
  }

  override fun intersects(r: Rectangle2D): Boolean {
    return intersects(r.minX, r.minY, r.width, r.height)
  }

  override fun intersects(x: Double, y: Double, w: Double, h: Double): Boolean {
    val r = createRect(x, y, w, h)
    return geom.intersects(r)
  }

  abstract class GeometryPathIterator internal constructor(
    protected val at: AffineTransform
  ) : PathIterator {
    protected var index = 0

    override fun getWindingRule(): Int {
      return PathIterator.WIND_EVEN_ODD
    }

    override fun next() {
      index++
    }
  }

  class PointPathIterator(
    private val point: Point,
    transform: AffineTransform
  ) : GeometryPathIterator(transform) {

    override fun currentSegment(coords: FloatArray): Int {
      return when (index) {
        0 -> {
          coords[0] = point.x.toFloat()
          coords[1] = point.y.toFloat()
          at.transform(coords, 0, coords, 0, 1)
          PathIterator.SEG_MOVETO
        }
        1 -> PathIterator.SEG_CLOSE
        else -> throw IllegalStateException()
      }
    }

    override fun currentSegment(coords: DoubleArray): Int {
      return when (index) {
        0 -> {
          coords[0] = point.x
          coords[1] = point.y
          at.transform(coords, 0, coords, 0, 1)
          PathIterator.SEG_MOVETO
        }
        1 -> PathIterator.SEG_CLOSE
        else -> throw IllegalStateException()
      }
    }

    override fun isDone(): Boolean {
      return index > 1
    }
  }

  open class LineStringPathIterator(
    lineString: LineString,
    transform: AffineTransform
  ) : GeometryPathIterator(transform) {
    internal val ring: Boolean
    internal var coordinates: CoordinateSequence

    init {
      coordinates = lineString.coordinateSequence
      ring = lineString is LinearRing
    }

    /**
     * Only to be called from PolygonPathIterator subclass
     *
     * @param coordinates A coordinate sequence to be used.
     */
    protected fun reInit(coordinates: CoordinateSequence) {
      this.coordinates = coordinates
      this.index = 0
    }

    override fun currentSegment(coords: FloatArray): Int {
      return when {
        index == 0 -> {
          coords[0] = coordinates.getOrdinate(index, 0).toFloat()
          coords[1] = coordinates.getOrdinate(index, 1).toFloat()
          at.transform(coords, 0, coords, 0, 1)
          PathIterator.SEG_MOVETO
        }
        index < coordinates.size() -> {
          coords[0] = coordinates.getOrdinate(index, 0).toFloat()
          coords[1] = coordinates.getOrdinate(index, 1).toFloat()
          at.transform(coords, 0, coords, 0, 1)
          PathIterator.SEG_LINETO
        }
        ring && index == coordinates.size() -> PathIterator.SEG_CLOSE
        else -> throw IllegalStateException()
      }
    }

    override fun currentSegment(coords: DoubleArray): Int {
      when {
        index == 0 -> {
          coords[0] = coordinates.getOrdinate(index, 0)
          coords[1] = coordinates.getOrdinate(index, 1)
          at.transform(coords, 0, coords, 0, 1)
          return PathIterator.SEG_MOVETO
        }
        index < coordinates.size() -> {
          coords[0] = coordinates.getOrdinate(index, 0)
          coords[1] = coordinates.getOrdinate(index, 1)
          at.transform(coords, 0, coords, 0, 1)
          return PathIterator.SEG_LINETO
        }
        else -> return if (ring && index == coordinates.size()) {
          PathIterator.SEG_CLOSE
        } else {
          throw IllegalStateException()
        }
      }
    }

    override fun isDone(): Boolean {
      return if (ring) index > coordinates.size() else index >= coordinates.size()
    }
  }

  class PolygonPathIterator(
    private val pg: Polygon,
    transform: AffineTransform
  ) : LineStringPathIterator(pg.exteriorRing, transform) {

    private var outerIndex = -1

    init {
      index = -1
    }

    override fun isDone(): Boolean {
      return outerIndex >= pg.numInteriorRing
    }

    override fun next() {
      super.next()
      if (super.isDone()) {
        outerIndex++
        if (outerIndex < pg.numInteriorRing) {
          super.reInit(pg.getInteriorRingN(outerIndex).coordinateSequence)
        }
      }
    }
  }

  class GeometryCollectionPathIterator(
    private val coll: GeometryCollection,
    transform: AffineTransform
  ) : GeometryPathIterator(transform) {

    internal var current: GeometryPathIterator

    init {
      current = pathIterator(coll.getGeometryN(index), transform)
    }

    override fun isDone(): Boolean {
      return index > coll.numGeometries
    }

    override fun next() {
      current.next()
      if (current.isDone) {
        index++
        if (index < coll.numGeometries) {
          current = pathIterator(coll.getGeometryN(index), at)
        }
      }
    }

    override fun currentSegment(coords: FloatArray): Int {
      return current.currentSegment(coords)
    }

    override fun currentSegment(coords: DoubleArray): Int {
      return current.currentSegment(coords)
    }
  }

  companion object {
    private val NO_SHELLS = arrayOf<LinearRing>()
    private val GEOMETRY_FACTORY = GeometryFactory()

    fun pathIterator(geometry: Geometry, transform: AffineTransform): GeometryPathIterator {
      return when (geometry) {
        is Point -> PointPathIterator(geometry, transform)
        is LineString -> LineStringPathIterator(geometry, transform)
        is Polygon -> PolygonPathIterator(geometry, transform)
        else -> GeometryCollectionPathIterator(geometry as GeometryCollection, transform)
      }
    }
  }
}
