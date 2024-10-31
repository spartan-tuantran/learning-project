package com.alext.rtree.misc.jts

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier
import org.locationtech.jts.simplify.TopologyPreservingSimplifier

enum class SimplifierAlgorithm {
  /**
   * Simplifies a geometry and ensures that
   * the result is a valid geometry having the
   * same dimension and number of components as the input,
   * and with the components having the same topological
   * relationship.
   *
   * If the input is a polygonal geometry, i.e Polygon or MultiPolygon
   * - The result has the same number of shells and holes as the input,
   * with the same topological structure
   * - The result rings touch at <b>no more</b> than the number of touching points in the input
   * (although they may touch at fewer points).
   * - The key implication of this statement is that if the
   * input is topologically valid, so is the simplified output.
   *
   * For linear geometries, if the input does not contain
   * any intersecting line segments, this property
   * will be preserved in the output.
   *
   * For all geometry types, the result will contain
   * enough vertices to ensure validity.  For polygons
   * and closed linear geometries, the result will have at
   * least 4 vertices; for open linestrings the result
   * will have at least 2 vertices.
   *
   * All geometry types are handled.
   * Empty and point geometries are returned unchanged.
   * Empty geometry components are deleted.
   *
   * The simplification uses a maximum-distance difference algorithm
   * similar to the Douglas-Peucker algorithm.
   *
   * --------------
   * - Known bugs -
   * --------------
   * 1. May create invalid topology if there are components which are
   * small relative to the tolerance value. In particular, if a small hole is very near an edge,
   * it is possible for the edge to be moved by a relatively large tolerance value and end up with the
   * hole outside the result shell (or inside another hole).
   *
   * 2. Similarly, it is possible for a small polygon component to end up inside
   * a nearby larger polygon. A workaround is to test for this situation in post-processing and remove
   * any invalid holes or polygons.
   */
  TOPOLOGY_PRESERVING {
    override fun simplify(geometry: Geometry, tolerance: Double): Geometry {
      return TopologyPreservingSimplifier.simplify(geometry, tolerance)
    }
  },

  /**
   * Simplifies a [org.locationtech.jts.geom.Geometry] using the Douglas-Peucker algorithm.
   * Ensures that any polygonal geometries returned are valid.
   * Simple lines are not guaranteed to remain simple after simplification.
   * All geometry types are handled.
   * Empty and point geometries are returned unchanged.
   * Empty geometry components are deleted.
   *
   * Note that in general D-P does not preserve topology -
   * e.g. polygons can be split, collapse to lines or disappear
   * holes can be created or disappear,
   * and lines can cross.
   * To simplify geometry while preserving topology use {@link TopologyPreservingSimplifier}.
   * (However, using D-P is significantly faster).
   *
   * ---------------
   * - Known bugs: -
   * ---------------
   * 1. In some cases the approach used to clean invalid simplified polygons
   * 2. Can distort the output geometry severely.
   */
  DOUGLAS_PEUCKER {
    override fun simplify(geometry: Geometry, tolerance: Double): Geometry {
      return DouglasPeuckerSimplifier.simplify(geometry, tolerance)
    }
  };

  abstract fun simplify(geometry: Geometry, tolerance: Double): Geometry
}

/**
 * Simplify a polygon with tolerance using default algorithm [SimplifierAlgorithm.DOUGLAS_PEUCKER] to optimize
 * for speed.
 *
 * @param tolerance The distance tolerance for the simplification. All vertices in the simplified geometry
 * will be within this distance of the original geometry. The tolerance value must be non-negative. A tolerance value
 * of zero is effectively a no-op.
 */
fun PostgisPolygon.simplify(tolerance: Double, algorithm: SimplifierAlgorithm = SimplifierAlgorithm.DOUGLAS_PEUCKER): PostgisPolygon {
  return algorithm.simplify(toJtsGeometry(), tolerance).toPostgisGeometry()
}
