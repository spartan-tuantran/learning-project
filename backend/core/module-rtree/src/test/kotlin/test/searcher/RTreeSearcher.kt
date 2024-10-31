package test.searcher

import com.alext.rtree.api.Entry
import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.extension.jts.GeoPolygon
import com.alext.rtree.extension.jts.geoPoint
import com.alext.rtree.extension.jts.geoPolygon
import com.alext.rtree.misc.jts.area
import com.alext.rtree.misc.jts.toJtsGeometry
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.UUID
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import test.ConsoleLogging
import test.Region
import test.measure

class RTreeSearcher(
  private val zones: Map<UUID, Region>
) : ConsoleLogging, Searcher {

  private val regionCache = Caffeine
    .newBuilder()
    .build<UUID, Region> { key ->
      zones[key]
    }

  private val areaCache = Caffeine
    .newBuilder()
    .build<UUID, Double> { key ->
      val result = regionCache.get(key)?.region?.area() ?: 0.0
      purple {
        "area: $result"
      }
      result
    }

  private val rtree = RTree
    .builder<UUID, GeoPolygon>()
    .maxChildren(4)
    .create(
      zones.map { (id, zone) ->
        Entry.create(
          value = id,
          geometry = Geometries.geoPolygon(zone.region.toJtsGeometry() as Polygon)
        )
      }
    )

  init {
    zones.forEach { (key, value) ->
      regionCache.put(key, value)
      areaCache.put(key, value.region.area())
    }
    purple("area cache size: ${regionCache.estimatedSize()}")
  }

  override fun search(point: Point): List<Region> {
    val target = Geometries.geoPoint(point)
    var iterations = 0
    val result = measure {
      rtree
        .search(target)
        .filter {
          iterations++
          it.geometry.intersects(point)
        }
        .mapNotNull { regionCache[it.value] }
    }
    return result.data.also {
      green("r-tree search(point) took ${result.ns} (ns), iterations=$iterations/${zones.size}")
    }
  }

  override fun search(polygon: Polygon): List<Region> {
    val target = Geometries.geoPolygon(polygon)
    var iterations = 0
    val result = measure {
      rtree
        .search(target)
        .filter {
          ++iterations
          it.geometry.intersects(polygon)
        }
        .mapNotNull { regionCache[it.value] }
    }
    return result.data.also {
      green("r-tree search(polygon) took ${result.ns} (ns), iterations=$iterations/${zones.size}")
    }
  }
}
