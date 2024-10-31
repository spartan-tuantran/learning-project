/**
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
package com.alext.database.postgis

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.CoordinateSequence
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.impl.PackedCoordinateSequence
import org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory
import org.postgis.binary.ByteGetter
import org.postgis.binary.ByteGetter.BinaryByteGetter
import org.postgis.binary.ByteGetter.StringByteGetter
import org.postgis.binary.ValueGetter

/**
 * Parse binary representation of geometries. Currently, only text rep (hexed)
 * implementation is tested.
 *
 * It should be easy to add char[] and CharSequence ByteGetter instances,
 * although the latter one is not compatible with older jdks.
 *
 * I did not implement real unsigned 32-bit integers or emulate them with long,
 * as both java Arrays and Strings currently can have only 2^31-1 elements
 * (bytes), so we cannot even get or build Geometries with more than approx.
 * 2^28 coordinates (8 bytes each).
 *
 * @author Markus Schaber, markus.schaber@logix-tt.com
 */
class JtsBinaryParser {

  private val factory = JtsSpatialContextFactory()

  /**
   * Parse a hex encoded geometry
   *
   * @param value String containing the hex data to be parsed
   * @return the resulting parsed geometry
   */
  fun parse(value: String): Geometry {
    val bytes = StringByteGetter(value)
    return parseGeometry(valueGetterForEndian(bytes))
  }

  /**
   * Parse a binary encoded geometry.
   *
   * @param value byte array containing the binary encoded geometry
   * @return the resulting parsed geometry
   */
  fun parse(value: ByteArray): Geometry {
    val bytes = BinaryByteGetter(value)
    return parseGeometry(valueGetterForEndian(bytes))
  }

  /**
   * Parse with a known geometry factory
   *
   * @param data ValueGetter for the data to be parsed
   * @param srid the SRID to be used for parsing
   * @param inheritSrid flag to toggle inheriting SRIDs
   * @return The resulting Geometry
   */
  @Suppress("NAME_SHADOWING")
  private inline fun <reified T : Geometry> parseGeometry(data: ValueGetter, srid: Int = 0, inheritSrid: Boolean = false): T {
    var srid = srid
    val endian = data.byte // skip and test endian flag
    require(endian == data.endian) { "Endian inconsistency!" }
    val typeWord = data.int
    val realType = typeWord and 0x1FFFFFFF // cut off high flag bits
    val haveZ = typeWord and -0x80000000 != 0
    val haveM = typeWord and 0x40000000 != 0
    val haveS = typeWord and 0x20000000 != 0
    if (haveS) {
      val newSrid = org.postgis.Geometry.parseSRID(data.int)
      require(!(inheritSrid && newSrid != srid)) {
        "Inconsistent SRID in complex geometry: $srid, $newSrid"
      }
      srid = newSrid
    } else if (!inheritSrid) {
      srid = org.postgis.Geometry.UNKNOWN_SRID
    }

    val result = when (realType) {
      org.postgis.Geometry.POINT -> parsePoint(data, haveZ, haveM)
      org.postgis.Geometry.LINESTRING -> parseLineString(data, haveZ, haveM)
      org.postgis.Geometry.POLYGON -> parsePolygon(data, haveZ, haveM, srid)
      org.postgis.Geometry.MULTIPOINT -> parseMultiPoint(data, srid)
      org.postgis.Geometry.MULTILINESTRING -> parseMultiLineString(data, srid)
      org.postgis.Geometry.MULTIPOLYGON -> parseMultiPolygon(data, srid)
      org.postgis.Geometry.GEOMETRYCOLLECTION -> parseCollection(data, srid)
      else -> throw IllegalArgumentException("Unknown Geometry Type!")
    }
    result.srid = srid
    return result as T
  }

  private fun parsePoint(data: ValueGetter, haveZ: Boolean, haveM: Boolean): Point {
    val x = data.double
    val y = data.double
    val result = if (haveZ) {
      val z = data.double
      factory.geometryFactory.createPoint(Coordinate(x, y, z))
    } else {
      factory.geometryFactory.createPoint(Coordinate(x, y))
    }

    if (haveM) { // skip M value
      data.double
    }
    return result
  }

  /**
   * Parse an Array of "slim" Points (without endianness and type, part of
   * LinearRing and Linestring, but not MultiPoint!
   *
   * @param haveZ
   * @param haveM
   */
  private fun parseCoordinates(data: ValueGetter, haveZ: Boolean, haveM: Boolean): CoordinateSequence {
    val count = data.int
    val dims = if (haveZ) 3 else 2
    val cs = PackedCoordinateSequence.Double(count, dims, 0)
    for (i in 0 until count) {
      for (d in 0 until dims) {
        cs.setOrdinate(i, d, data.double)
      }
      if (haveM) { // skip M value
        data.double
      }
    }
    return cs
  }

  private fun parseMultiPoint(data: ValueGetter, srid: Int): MultiPoint {
    return factory.geometryFactory.createMultiPoint(
      Array(data.int) {
        parseGeometry<Point>(data, srid, true)
      }
    )
  }

  private fun parseLineString(data: ValueGetter, haveZ: Boolean, haveM: Boolean): LineString {
    return factory.geometryFactory.createLineString(parseCoordinates(data, haveZ, haveM))
  }

  private fun parseLinearRing(data: ValueGetter, haveZ: Boolean, haveM: Boolean): LinearRing {
    return factory.geometryFactory.createLinearRing(parseCoordinates(data, haveZ, haveM))
  }

  private fun parsePolygon(data: ValueGetter, haveZ: Boolean, haveM: Boolean, srid: Int): Polygon {
    val holeCount = data.int - 1
    val shell = parseLinearRing(data, haveZ, haveM)
    shell.srid = srid
    val rings = Array(holeCount) {
      val ring = parseLinearRing(data, haveZ, haveM)
      ring.srid = srid
      ring
    }
    return factory.geometryFactory.createPolygon(shell, rings)
  }

  private fun parseMultiLineString(data: ValueGetter, srid: Int): MultiLineString {
    return factory.geometryFactory.createMultiLineString(
      Array(data.int) {
        parseGeometry(data, srid, true)
      }
    )
  }

  private fun parseMultiPolygon(data: ValueGetter, srid: Int): MultiPolygon {
    return factory.geometryFactory.createMultiPolygon(
      Array(data.int) {
        parseGeometry(data, srid, true)
      }
    )
  }

  private fun parseCollection(data: ValueGetter, srid: Int): GeometryCollection {
    return factory.geometryFactory.createGeometryCollection(
      Array(data.int) {
        parseGeometry(data, srid, true)
      }
    )
  }

  companion object {
    /**
     * Get the appropriate ValueGetter for my endianness
     *
     * @param bytes The appropriate Byte Getter
     * @return the ValueGetter
     */
    fun valueGetterForEndian(bytes: ByteGetter): ValueGetter {
      return when (bytes.get(0)) {
        ValueGetter.XDR.NUMBER.toInt() -> ValueGetter.XDR(bytes)
        ValueGetter.NDR.NUMBER.toInt() -> ValueGetter.NDR(bytes)
        else -> throw IllegalArgumentException("Unknown Endian type: ${bytes.get(0)}")
      }
    }
  }
}
