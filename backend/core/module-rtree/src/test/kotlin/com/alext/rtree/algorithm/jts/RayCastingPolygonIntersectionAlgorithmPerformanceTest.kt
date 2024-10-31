package com.alext.rtree.algorithm.jts

import com.alext.rtree.misc.jts.toJtsGeometry
import kotlin.system.measureNanoTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.locationtech.jts.geom.Polygon
import test.AbstractRTreeTest

@EnabledIfEnvironmentVariable(named = "test", matches = "local")
class RayCastingPolygonIntersectionAlgorithmPerformanceTest : AbstractRTreeTest(), RayCastingPolygonIntersectionAlgorithm {

  @Test
  fun `point in polygon - use center`() {
    var slow = 0L
    var fast = 0L
    repeat(100) {
      REGIONS.forEach { (_, zone) ->
        val polygon = zone.region.toJtsGeometry<Polygon>()
        slow += measureNanoTime {
          polygon.intersects(polygon.centroid)
        }
        fast += measureNanoTime {
          polygon.intersectsWith(polygon.centroid)
        }
      }
    }
    red("jts: $slow")
    green("ray-casting: $fast")
    purple("speedup: ${slow.toDouble() / fast.toDouble()}%")
  }
}
