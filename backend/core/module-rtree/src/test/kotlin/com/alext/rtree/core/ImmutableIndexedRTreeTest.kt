@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "JAVA_CLASS_ON_COMPANION")

package com.alext.rtree.core

import com.alext.rtree.Item
import com.alext.rtree.api.Entry
import com.alext.rtree.api.IndexedRTree
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Point
import com.alext.rtree.extension.jts.GeoPolygon
import com.alext.rtree.extension.jts.JtsPolygon
import com.alext.rtree.extension.jts.geoPolygon
import com.alext.rtree.misc.jts.toJtsGeometry
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.postgis.Polygon
import test.AbstractRTreeTest

class ImmutableIndexedRTreeTest : AbstractRTreeTest() {

  private data class Region(
    val id: UUID,
    val region: Polygon
  )

  companion object {
    private val REGIONS = javaClass.classLoader.getResourceAsStream("regions.csv").bufferedReader()
      .lines()
      .map {
        val values = it.split(";")
        Region(
          id = UUID.fromString(values[0]),
          region = Polygon(values[3])
        )
      }
      .toList()
  }

  private val factory = Geometries
  private val items = listOf(
    Item(1, 1.0, 2.0),
    Item(2, 2.0, 2.0),
    Item(3, 1.0, 1.0),
    Item(4, 0.0, 0.0),
    Item(5, -1.0, -1.0)
  )

  @Test
  fun `empty tree has empty index`() {
    val tree = IndexedRTree.builder<Int, Point>().create()
    val index = tree.index
    assertThat(index).isEmpty()
  }

  @Test
  fun `insert same item is overriding`() {
    val tree = IndexedRTree
      .builder<Int, Point>()
      .create()
      .add(1, factory.point(1.0, 2.0))
      .add(1, factory.point(2.0, 3.0))

    val result = tree.index.getValue(1)
    assertThat(result.geometry.x).isEqualTo(2.0)
    assertThat(result.geometry.y).isEqualTo(3.0)
  }

  @Test
  fun `index should return correct item`() {
    val tree = items.fold(
      IndexedRTree.builder<Int, Point>().create()
    ) { tree, item ->
      tree.add(item.value, factory.point(item.x, item.y))
    }
    val index = tree.index
    val entries = tree.entries()
    items.forEach { item ->
      assertThat(index[item.value]).isEqualTo(entries.first { it.value == item.value })
    }
    assertThat(tree.size()).isEqualTo(items.size)
  }

  @Test
  fun `packing insert should return correct item`() {
    val tree = IndexedRTree.builder<Int, Point>().create(
      items.map { item ->
        Entry.create(item.value, factory.point(item.x, item.y))
      }
    )
    val index = tree.index
    val entries = tree.entries()
    // They must point to the same entries to avoid copying expensive geometry
    items.forEach { item ->
      assertThat(index[item.value] === entries.first { it.value == item.value }).isEqualTo(true)
    }
  }

  @Test
  fun `packing insert exhaustive test`() {
    val tree = IndexedRTree.builder<UUID, GeoPolygon>().create(
      REGIONS.map { item ->
        Entry.create(item.id, Geometries.geoPolygon(item.region.toJtsGeometry<JtsPolygon>()))
      }
    )

    val index = tree.index
    assertThat(index.size).isEqualTo(REGIONS.size)
    REGIONS.forEach { z ->
      assertThat(index[z.id] === index[z.id]).isEqualTo(true)
    }
  }

  @Test
  fun `insert then delete should maintain index`() {
    val entries = items.map { item ->
      Entry.create(item.value, factory.point(item.x, item.y))
    }
    val tree = IndexedRTree
      .builder<Int, Point>()
      .create(entries)
      .remove(entries[0])

    val index = tree.index
    assertThat(index[items[0].value]).isEqualTo(null)
    assertThat(index[items[1].value]).isEqualTo(entries[1])
  }

  @Test
  fun `insert items with same geometry and remove`() {
    var tree = IndexedRTree
      .builder<Int, Point>()
      .create()
      .add(0, factory.point(2.0, 2.0))
      .add(1, factory.point(2.0, 2.0))

    assertThat(tree.size()).isEqualTo(2)
    assertThat(tree.search(Geometries.point(2.0, 2.0))).containsExactly(
      Entry.create(0, factory.point(2.0, 2.0)),
      Entry.create(1, factory.point(2.0, 2.0))
    )

    tree = tree.remove(Entry.create(0, factory.point(2.0, 2.0)))
    assertThat(tree.size()).isEqualTo(1)
    tree = tree.remove(Entry.create(1, factory.point(2.0, 2.0)))
    assertThat(tree.size()).isEqualTo(0)
  }
}
