/**
 * Wrapper for PostgreSQL JDBC driver to allow transparent reading and writing
 * of JTS geometries
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

import java.sql.SQLException
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory
import org.locationtech.jts.io.WKTReader
import org.postgresql.util.PGobject

/**
 * JTS Geometry SQL wrapper. Supports PostGIS 1.x (lwgeom hexwkb) for writing
 * and both PostGIS 0.x (EWKT) and 1.x (lwgeom hexwkb) for reading.
 *
 * @author Markus Schaber
 */
class JtsPgGeometry() : PGobject() {

  lateinit var geometry: Geometry
    private set

  init {
    setType("geometry")
  }

  constructor(geom: Geometry) : this() {
    this.geometry = geom
  }

  @Throws(SQLException::class)
  constructor(value: String) : this() {
    setValue(value)
  }

  override fun toString(): String {
    return geometry.toString()
  }

  override fun getValue(): String {
    return BINARY_WRITER.writeHexed(geometry)
  }

  @Throws(SQLException::class)
  override fun setValue(value: String?) {
    from(value)?.let {
      geometry = it
    }
  }

  override fun clone(): Any {
    val jts = JtsPgGeometry(geometry)
    jts.setType(type)
    return jts
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + geometry.hashCode()
    return result
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JtsPgGeometry) return false
    if (!super.equals(other)) return false
    if (geometry != other.geometry) return false
    return true
  }

  companion object {
    private val BINARY_PARSER = JtsBinaryParser()
    private val BINARY_WRITER = JtsBinaryWriter()
    private val WKT_READER = WKTReader(GeometryFactory(PrecisionModel(), 0, PackedCoordinateSequenceFactory.DOUBLE_FACTORY))

    /* JDK 1.5 Serialization */
    const val serialVersionUID: Long = 0x100

    @Throws(SQLException::class)
    fun from(string: String?): Geometry? {
      var value = string ?: return null
      try {
        value = value.trim { it <= ' ' }
        return if (value.startsWith("00") || value.startsWith("01")) {
          BINARY_PARSER.parse(value)
        } else {
          // No srid := 0 in JTS world
          var srid = 0
          // Break up geometry into srid and wkt
          val geom = if (value.startsWith("SRID=")) {
            val temp = value.split(";")
            srid = temp[0].substring(5).toInt()
            WKT_READER.read(temp[1].trim())
          } else {
            WKT_READER.read(value)
          }
          geom.setSridRecursively(srid)
          geom
        }
      } catch (e: Exception) {
        e.printStackTrace()
        throw SQLException("Error parsing SQL data: $e")
      }
    }

    /**
     * Recursively set a srid for the geometry and all sub-geometries
     *
     * @receiver geom The geometry to work on
     * @param srid The SRID to be set to
     */
    internal fun Geometry.setSridRecursively(srid: Int) {
      setSRID(srid)
      if (this is GeometryCollection) {
        val count = getNumGeometries()
        for (i in 0 until count) {
          getGeometryN(i).setSridRecursively(srid)
        }
      } else if (this is Polygon) {
        exteriorRing.srid = srid
        val count = numInteriorRing
        for (i in 0 until count) {
          getInteriorRingN(i).srid = srid
        }
      }
    }
  }
}
