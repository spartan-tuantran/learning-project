package com.alext.rtree.jts

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.extension.jts.taggableGeoPolygon
import com.alext.rtree.misc.jts.area
import com.alext.rtree.misc.jts.toJtsGeometry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Polygon
import test.AbstractRTreeTest

class TaggableGeoPolygonTest : AbstractRTreeTest() {

  private val zones = REGIONS

  @Test
  fun `add tag and use tag`() {
    val geometries = zones.map { (_, zone) ->
      val polygon = zone.region.toJtsGeometry<Polygon>()
      Geometries.taggableGeoPolygon(polygon, zone.region.area())
    }
    val sorted = geometries.sortedBy { it.tag ?: Double.MAX_VALUE }
    assertThat(sorted).isSortedAccordingTo { l, r -> l.tag!!.compareTo(r.tag!!) }
  }
}
