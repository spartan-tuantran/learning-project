package com.alext.rtree.core.geometry

/**
 * A geometrical region that represents an Entry spatially. It is recommended
 * that implementations of this interface implement equals() and hashCode()
 * appropriately that entry equality checks work as expected.
 */
interface Geometry {
  /**
   * The minimum bounding rectangle of this geometry.
   */
  val mbr: Rectangle

  /**
   * Returns the distance to the given [Rectangle]. For a [Rectangle]
   * this might be Euclidean distance but for an EPSG4326 lat-long Rectangle might
   * be great-circle distance. The distance function should satisfy the following
   * properties:
   *
   * `distance(r) >= 0`
   * `if r1 contains r2 then distance(r1) <= distance(r2)`
   *
   * @param rectangle The rectangle to measure distance to
   */
  fun distance(rectangle: Rectangle): Double

  /**
   * Return true if this geometry intersects with [Rectangle]
   *
   * @param rectangle The rectangle to be checked
   */
  fun intersects(rectangle: Rectangle): Boolean
}
