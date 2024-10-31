package com.alext.database.exposed.function

import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.doubleLiteral
import org.jetbrains.exposed.sql.stringParam
import org.locationtech.jts.geom.Geometry

class IntersectFunction(e: Expression<*>, g: Geometry) :
  CustomFunction<Boolean>("st_intersects", BooleanColumnType(), e, stringParam("SRID=4326;" + g.toText()))

fun <G : Geometry> ExpressionWithColumnType<G>.intersect(geometry: Geometry): Op<Boolean> {
  return IntersectFunction(this, geometry).eq(true)
}

class WithinFunction(e: Expression<*>, g: Geometry) :
  CustomFunction<Boolean>("st_within", BooleanColumnType(), e, stringParam("SRID=4326;" + g.toText()))

fun <G : Geometry> ExpressionWithColumnType<G>.within(geometry: Geometry): Op<Boolean> {
  return WithinFunction(this, geometry).eq(true)
}

class OverlapFunction(e: Expression<*>, g: Geometry) :
  CustomFunction<Boolean>("st_overlaps", BooleanColumnType(), e, stringParam("SRID=4326;" + g.toText()))

fun <G : Geometry> ExpressionWithColumnType<G>.overlap(geometry: Geometry): Op<Boolean> {
  return OverlapFunction(this, geometry).eq(true)
}

class ContainFunction(e: Expression<*>, g: Geometry) :
  CustomFunction<Boolean>("st_contains", BooleanColumnType(), e, stringParam("SRID=4326;" + g.toText()))

fun <G : Geometry> ExpressionWithColumnType<G>.contain(geometry: Geometry): Op<Boolean> {
  return ContainFunction(this, geometry).eq(true)
}

class DistanceFunction(e: Expression<*>, g: Geometry) :
  CustomFunction<Double>("st_distance", DoubleColumnType(), e, stringParam("SRID=4326;" + g.toText()))

fun <G : Geometry> ExpressionWithColumnType<G>.distance(geometry: Geometry): DistanceFunction {
  return DistanceFunction(this, geometry)
}

class WithinDistanceFunction(e: Expression<*>, g: Geometry, radius: Double) :
  CustomFunction<Boolean>("st_dwithin", BooleanColumnType(), e, stringParam("SRID=4326;" + g.toText()), doubleLiteral(radius))

fun <G : Geometry> ExpressionWithColumnType<G>.withinDistance(geometry: Geometry, distance: Double): Op<Boolean> {
  return WithinDistanceFunction(this, geometry, distance).eq(true)
}
