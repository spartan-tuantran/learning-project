package com.alext.rtree.misc.postgis

import org.postgis.LinearRing
import org.postgis.MultiPolygon
import org.postgis.Point
import org.postgis.Polygon

fun Polygon.toMultiPolygon(): MultiPolygon {
  return MultiPolygon(listOf(this).toTypedArray())
}

fun LinearRing.points(): List<Point> {
  return List<Point>(
    numPoints(),
    init = { i ->
      getPoint(i)
    }
  )
}

fun Polygon.rings(): List<LinearRing> {
  return List<LinearRing>(
    numRings(),
    init = { i ->
      getRing(i)
    }
  )
}

fun MultiPolygon.polygons(): List<Polygon> {
  return List<Polygon>(
    numPolygons(),
    init = { i ->
      getPolygon(i)
    }
  )
}
