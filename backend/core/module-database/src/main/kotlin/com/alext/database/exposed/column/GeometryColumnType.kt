package com.alext.database.exposed.column

import com.alext.database.postgis.JtsPgGeometry
import kotlin.reflect.KClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.postgresql.util.PGobject

class GeometryColumnType<G : Geometry>(
  private val clazz: KClass<G>
) : ColumnType() {

  override fun sqlType() = "GEOMETRY(${clazz.simpleName}, 4326)"

  override fun valueFromDB(value: Any): Any {
    return if (value is PGobject) {
      JtsPgGeometry.from(value.value) ?: value
    } else {
      value
    }
  }

  override fun notNullValueToDB(value: Any): Any {
    return if (value is Geometry) {
      JtsPgGeometry(value)
    } else {
      value
    }
  }
}

inline fun <reified G : Geometry> Table.geometry(name: String): Column<G> {
  return registerColumn(name, GeometryColumnType(G::class))
}

fun Table.point(name: String): Column<Point> {
  return geometry(name)
}

fun Table.polygon(name: String): Column<Polygon> {
  return geometry(name)
}

fun Table.multiPolygon(name: String): Column<MultiPolygon> {
  return geometry(name)
}

fun Table.lineString(name: String): Column<LineString> {
  return geometry(name)
}
