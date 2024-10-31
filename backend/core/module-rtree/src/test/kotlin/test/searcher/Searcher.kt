package test.searcher

import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import test.Region

interface Searcher {
  fun search(point: Point): List<Region>
  fun search(polygon: Polygon): List<Region>
}
