package com.alext.rtree.misc.jts

fun PostgisGeometry.intersects(other: PostgisGeometry): Boolean {
  return toJtsGeometry().intersects(other.toJtsGeometry())
}

// The intersectionPattern string here is using the DE9IM notation
// for "interiors intersect and we don't care about boundaries or exteriors"
fun PostgisGeometry.interiorIntersectsInterior(other: PostgisGeometry): Boolean {
  return toJtsGeometry().relate(other.toJtsGeometry(), "T********")
}

fun PostgisGeometry.crosses(other: PostgisGeometry): Boolean {
  return toJtsGeometry().crosses(other.toJtsGeometry())
}

fun PostgisGeometry.within(other: PostgisGeometry): Boolean {
  return toJtsGeometry().within(other.toJtsGeometry())
}

fun PostgisGeometry.contains(other: PostgisGeometry): Boolean {
  return toJtsGeometry().contains(other.toJtsGeometry())
}

fun PostgisGeometry.overlaps(other: PostgisGeometry): Boolean {
  return toJtsGeometry().overlaps(other.toJtsGeometry())
}

fun PostgisGeometry.covers(other: PostgisGeometry): Boolean {
  return toJtsGeometry().covers(other.toJtsGeometry())
}

fun PostgisGeometry.disjoint(other: PostgisGeometry): Boolean {
  return toJtsGeometry().disjoint(other.toJtsGeometry())
}

fun PostgisGeometry.touches(other: PostgisGeometry): Boolean {
  return toJtsGeometry().touches(other.toJtsGeometry())
}

/**
 * PostgisGeometry#equals fails if points are re-ordered. This does not.
 */
fun PostgisGeometry.isSame(other: PostgisGeometry): Boolean {
  return toJtsGeometry().equals(other.toJtsGeometry())
}
