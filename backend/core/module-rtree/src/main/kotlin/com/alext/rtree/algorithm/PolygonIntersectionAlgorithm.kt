package com.alext.rtree.algorithm

interface PolygonIntersectionAlgorithm<Polygon, Point> {
  /**
   * Return true if the [Polygon] intersects with [Point]
   */
  fun Polygon.intersectsWith(point: Point): Boolean
}
