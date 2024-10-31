/**
 * PostGIS extension for PostgreSQL JDBC driver - Binary Writer
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

import org.locationtech.jts.geom.CoordinateSequence
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.postgis.binary.ByteSetter
import org.postgis.binary.ValueSetter

/**
 * Create binary representation of geometries. Currently, only text rep (hexed)
 * implementation is tested. Supports only 2 dimensional geometries.
 *
 * It should be easy to add char[] and CharSequence ByteGetter instances,
 * although the latter one is not compatible with older jdks.
 *
 * I did not implement real unsigned 32-bit integers or emulate them with long,
 * as both java Arrays and Strings currently can have only 2^31-1 elements
 * (bytes), so we cannot even get or build Geometries with more than approx.
 * 2^28 coordinates (8 bytes each).
 *
 * @author markus.schaber@logi-track.com
 */
class JtsBinaryWriter {
  /**
   * Write a hex encoded geometry
   *
   * Currently, geometries with more than 2 dimensions and measures are not
   * cleanly supported, but SRID is honored.
   *
   * @param geom The geometry to be written
   * @param REP The endianness representation to use for writing
   * @return String containing the hex-encoded geometry
   */
  @JvmOverloads
  fun writeHexed(geom: Geometry, REP: Byte = ValueSetter.NDR.NUMBER): String {
    val length = estimateBytes(geom)
    val bytes = ByteSetter.StringByteSetter(length)
    writeGeometry(geom, valueSetterForEndian(bytes, REP))
    return bytes.result()
  }

  /**
   * Write a binary encoded geometry.
   *
   *
   * Currently, geometries with more than 2 dimensions and measures are not
   * cleanly supported, but SRID is honored.
   *
   * @param geom The geometry to be written
   * @param endian The endianness representation to use for writing
   * @return byte array containing the encoded geometry
   */
  @JvmOverloads
  fun writeBinary(geom: Geometry, endian: Byte = ValueSetter.NDR.NUMBER): ByteArray {
    val length = estimateBytes(geom)
    val bytes = ByteSetter.BinaryByteSetter(length)
    writeGeometry(geom, valueSetterForEndian(bytes, endian))
    return bytes.result()
  }

  /**
   * Parse a geometry starting at offset.
   *
   * @param geom The geometry to be written
   * @param dest The dest to write to
   */
  private fun writeGeometry(geom: Geometry, dest: ValueSetter) {
    val dimension: Int
    if (geom.isEmpty) {
      // don't set any flag bits
      dimension = 0
    } else {
      dimension = getCoordDim(geom)
      require(!(dimension < 2 || dimension > 4)) { "Unsupported geometry dimensionality: $dimension" }
    }
    // write endian flag
    dest.setByte(dest.endian)

    // write typeword
    val plainType = ogisType(geom)
    var typeWord = plainType
    if (dimension == 3 || dimension == 4) {
      typeWord = typeWord or -0x80000000
    }
    if (dimension == 4) {
      typeWord = typeWord or 0x40000000
    }

    val haveSrid = checkSrid(geom)
    if (haveSrid) {
      typeWord = typeWord or 0x20000000
    }

    dest.setInt(typeWord)

    if (haveSrid) {
      dest.setInt(geom.srid)
    }

    when (plainType) {
      org.postgis.Geometry.POINT -> writePoint((geom as Point?)!!, dest)
      org.postgis.Geometry.LINESTRING -> writeLineString((geom as LineString?)!!, dest)
      org.postgis.Geometry.POLYGON -> writePolygon((geom as Polygon?)!!, dest)
      org.postgis.Geometry.MULTIPOINT -> writeMultiPoint((geom as MultiPoint?)!!, dest)
      org.postgis.Geometry.MULTILINESTRING -> writeMultiLineString(geom as MultiLineString, dest)
      org.postgis.Geometry.MULTIPOLYGON -> writeMultiPolygon(geom as MultiPolygon, dest)
      org.postgis.Geometry.GEOMETRYCOLLECTION -> writeCollection(geom as GeometryCollection, dest)
      else -> throw IllegalArgumentException("Unknown Geometry Type: $plainType")
    }
  }

  /**
   * Writes a "slim" Point (without endiannes, srid ant type, only the
   * ordinates and measure. Used by writeGeometry.
   */
  private fun writePoint(geom: Point, dest: ValueSetter) {
    writeCoordinates(geom.coordinateSequence, getCoordDim(geom), dest)
  }

  /**
   * Write a CoordinateSequence, part of LinearRing and Linestring, but not
   * MultiPoint!
   */
  private fun writeCoordinates(seq: CoordinateSequence, dims: Int, dest: ValueSetter) {
    for (i in 0 until seq.size()) {
      for (d in 0 until dims) {
        dest.setDouble(seq.getOrdinate(i, d))
      }
    }
  }

  private fun writeMultiPoint(geom: MultiPoint, dest: ValueSetter) {
    dest.setInt(geom.numPoints)
    for (i in 0 until geom.numPoints) {
      writeGeometry(geom.getGeometryN(i), dest)
    }
  }

  private fun writeLineString(geom: LineString, dest: ValueSetter) {
    dest.setInt(geom.numPoints)
    writeCoordinates(geom.coordinateSequence, getCoordDim(geom), dest)
  }

  private fun writePolygon(geom: Polygon, dest: ValueSetter) {
    dest.setInt(geom.numInteriorRing + 1)
    writeLineString(geom.exteriorRing, dest)
    for (i in 0 until geom.numInteriorRing) {
      writeLineString(geom.getInteriorRingN(i), dest)
    }
  }

  private fun writeMultiLineString(geom: MultiLineString, dest: ValueSetter) {
    writeGeometryArray(geom, dest)
  }

  private fun writeMultiPolygon(geom: MultiPolygon, dest: ValueSetter) {
    writeGeometryArray(geom, dest)
  }

  private fun writeCollection(geom: GeometryCollection, dest: ValueSetter) {
    writeGeometryArray(geom, dest)
  }

  private fun writeGeometryArray(geom: Geometry, dest: ValueSetter) {
    dest.setInt(geom.numGeometries)
    for (i in 0 until geom.numGeometries) {
      writeGeometry(geom.getGeometryN(i), dest)
    }
  }

  /**
   * Estimate how much bytes a geometry will need in WKB.
   *
   * @param geom Geometry to estimate
   * @return number of bytes needed
   */
  private fun estimateBytes(geom: Geometry): Int {
    var result = 0
    // write endian flag
    result += 1
    // write typeword
    result += 4

    if (checkSrid(geom)) {
      result += 4
    }

    result += when (ogisType(geom)) {
      org.postgis.Geometry.POINT -> estimatePoint(geom as Point)
      org.postgis.Geometry.LINESTRING -> estimateLineString(geom as LineString)
      org.postgis.Geometry.POLYGON -> estimatePolygon(geom as Polygon)
      org.postgis.Geometry.MULTIPOINT -> estimateMultiPoint(geom as MultiPoint)
      org.postgis.Geometry.MULTILINESTRING -> estimateMultiLineString(geom as MultiLineString)
      org.postgis.Geometry.MULTIPOLYGON -> estimateMultiPolygon(geom as MultiPolygon)
      org.postgis.Geometry.GEOMETRYCOLLECTION -> estimateCollection(geom as GeometryCollection)
      else -> throw IllegalArgumentException("Unknown Geometry Type: " + ogisType(geom))
    }
    return result
  }

  private fun checkSrid(geom: Geometry): Boolean {
    val srid = geom.srid
    return srid > 0
  }

  private fun estimatePoint(geom: Point): Int {
    return 8 * getCoordDim(geom)
  }

  /**
   * Write an Array of "full" Geometries
   */
  private fun estimateGeometryArray(container: Geometry): Int {
    var result = 0
    for (i in 0 until container.numGeometries) {
      result += estimateBytes(container.getGeometryN(i))
    }
    return result
  }

  /**
   * Estimate an array of "fat" Points
   */
  private fun estimateMultiPoint(geom: MultiPoint): Int {
    // int size
    var result = 4
    if (geom.numGeometries > 0) {
      // We can shortcut here, compared to estimateGeometryArray, as all
      // subgeoms have the same fixed size
      result += geom.numGeometries * estimateBytes(geom.getGeometryN(0))
    }
    return result
  }

  private fun estimateLineString(geom: LineString?): Int {
    return if (geom == null || geom.numGeometries == 0) {
      0
    } else {
      4 + 8 * getCoordSequenceDim(geom.coordinateSequence) * geom.coordinateSequence.size()
    }
  }

  private fun estimatePolygon(geom: Polygon): Int {
    // int length
    var result = 4
    result += estimateLineString(geom.exteriorRing)
    for (i in 0 until geom.numInteriorRing) {
      result += estimateLineString(geom.getInteriorRingN(i))
    }
    return result
  }

  private fun estimateMultiLineString(geom: MultiLineString): Int {
    // 4-byte count + subgeometries
    return 4 + estimateGeometryArray(geom)
  }

  private fun estimateMultiPolygon(geom: MultiPolygon): Int {
    // 4-byte count + subgeometries
    return 4 + estimateGeometryArray(geom)
  }

  private fun estimateCollection(geom: GeometryCollection): Int {
    // 4-byte count + subgeometries
    return 4 + estimateGeometryArray(geom)
  }

  companion object {

    /**
     * Get the appropriate ValueGetter for my endianness
     *
     * @param bytes The ByteSetter
     * @param endian The endian to be used
     * @return the appropriate ValueSetter for the specified endian
     */
    fun valueSetterForEndian(bytes: ByteSetter, endian: Byte): ValueSetter {
      return when (endian) {
        ValueSetter.XDR.NUMBER -> ValueSetter.XDR(bytes)
        ValueSetter.NDR.NUMBER -> ValueSetter.NDR(bytes)
        else -> throw IllegalArgumentException("Unknown Endian type:$endian")
      }
    }

    /**
     * We always write empty geometries as empty collections - for OpenGIS
     * conformance
     */
    fun ogisType(geom: Geometry): Int {
      return when {
        geom.isEmpty -> org.postgis.Geometry.GEOMETRYCOLLECTION
        geom is Point -> org.postgis.Geometry.POINT
        geom is LineString -> org.postgis.Geometry.LINESTRING
        geom is Polygon -> org.postgis.Geometry.POLYGON
        geom is MultiPoint -> org.postgis.Geometry.MULTIPOINT
        geom is MultiLineString -> org.postgis.Geometry.MULTILINESTRING
        geom is MultiPolygon -> org.postgis.Geometry.MULTIPOLYGON
        else -> if (geom is GeometryCollection) {
          org.postgis.Geometry.GEOMETRYCOLLECTION
        } else {
          throw IllegalArgumentException("Unknown Geometry Type: " + geom.javaClass.name)
        }
      }
    }

    fun getCoordDim(geom: Geometry): Int {
      if (geom.isEmpty) {
        return 0
      }
      return when (geom) {
        is Point -> getCoordSequenceDim(geom.coordinateSequence)
        is LineString -> getCoordSequenceDim(geom.coordinateSequence)
        is Polygon -> getCoordSequenceDim(geom.exteriorRing.coordinateSequence)
        else -> getCoordDim(geom.getGeometryN(0))
      }
    }

    fun getCoordSequenceDim(coords: CoordinateSequence?): Int {
      if (coords == null || coords.size() == 0) {
        return 0
      }
      // JTS has a really strange way to handle dimensions!
      // Just have a look at PackedCoordinateSequence and
      // CoordinateArraySequence
      val dimensions = coords.dimension
      return if (dimensions == 3) {
        // CoordinateArraySequence will always return 3, so we have to
        // check, if
        // the third ordinate contains NaN, then the geom is actually
        // 2-dimensional
        if (java.lang.Double.isNaN(coords.getOrdinate(0, CoordinateSequence.Z))) 2 else 3
      } else {
        dimensions
      }
    }
  }
}
