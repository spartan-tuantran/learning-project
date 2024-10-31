package com.alext.rtree.misc

import com.alext.rtree.extension.jts.JtsPolygon
import com.alext.rtree.extension.postgis.PostgisPolygon
import com.alext.rtree.misc.jts.toJtsGeometry

fun String.wktPostgisPolygon(): PostgisPolygon {
  return PostgisPolygon(this)
}

fun String.wktJtsPolygon(): JtsPolygon {
  return PostgisPolygon(this).toJtsGeometry() as JtsPolygon
}
