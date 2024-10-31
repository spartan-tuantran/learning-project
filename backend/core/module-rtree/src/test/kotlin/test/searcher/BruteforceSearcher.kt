package test.searcher

import com.alext.rtree.misc.jts.toJtsGeometry
import java.util.UUID
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import test.ConsoleLogging
import test.Region
import test.measure

/**
 * A tree that just go through all polygon one by
 * one and perform a search. We use this to test
 * the correctness of actual RTree
 */
class BruteforceSearcher(
  private val zones: Map<UUID, Region>
) : Searcher, ConsoleLogging {

  private val map: Map<UUID, Polygon> = zones.mapValues { it.value.region.toJtsGeometry<Polygon>() }

  /**
   * Search for all polygons that intersect with this point
   *
   * @param point The searching point
   */
  override fun search(point: Point): List<Region> {
    val result = measure {
      map
        .filter { point.intersects(it.value) }
        .mapNotNull { zones[it.key] }
    }
    return result.data.also {
      blue("bruteforce search(point) took ${result.ns} (ns)")
    }
  }

  /**
   * Search for all polygons that intersect with this polygon
   *
   * @param polygon The target polygon
   */
  override fun search(polygon: Polygon): List<Region> {
    val result = measure {
      map
        .filter { polygon.intersects(it.value) }
        .mapNotNull { zones[it.key] }
    }
    return result.data.also {
      blue("bruteforce search(polygon) took ${result.ns} (ns)")
    }
  }
}
