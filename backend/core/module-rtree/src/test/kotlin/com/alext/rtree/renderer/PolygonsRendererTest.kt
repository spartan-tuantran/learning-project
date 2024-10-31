package com.alext.rtree.renderer

import com.alext.rtree.extension.jts.JtsPolygon
import com.alext.rtree.misc.jts.toJtsGeometry
import java.util.UUID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.locationtech.jts.geom.Polygon
import test.AbstractRTreeTest

@EnabledIfEnvironmentVariable(named = "test", matches = "local")
class PolygonsRendererTest : AbstractRTreeTest() {

  @Test
  fun `render zone polygons`() {
    REGIONS
      .values
      .toList()
      .takeLast(500)
      .map { it.region.toJtsGeometry<JtsPolygon>() }
      .sortedBy { it.area }
      .renderer(500, 500)
      .saveTo("poly.png")
  }

  @Test
  fun `render region polygons`() {
    lines("/snapshot_00000000-0000-0000-0000-000000000000.tar.gz")
      .mapNotNull {
        try {
          JACKSON.readValue(it, Region::class.java).polygon
        } catch (e: Exception) {
          null
        }
      }
      .renderer(2000, 2000)
      .saveTo("poly.png")
  }
}

data class Region(
  val id: UUID = UUID.randomUUID(),
  val orgId: UUID = UUID(0, 0),
  val geohash: String? = null,
  val name: String = "",
  val description: String? = null,
  val kind: String,
  val global: Boolean = true,
  val polygon: Polygon
)
