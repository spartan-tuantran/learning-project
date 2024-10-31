package com.alext.rtree.core.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GroupPairTest {

  private val factory = Geometries

  @Test
  fun `marginSum of a group is sum of perimeter`() {
    val r1 = factory.rectangle(1.0, 2.0, 3.0, 4.0)
    val r2 = factory.rectangle(0.0, 0.0, 4.0, 4.0)
    val group = GroupPair.create(listOf(r1), listOf(r2))
    assertThat(group.marginSum).isEqualTo(24.0)
  }

  @Test
  fun `areaSum of a group is sum of area`() {
    val r1 = factory.rectangle(1.0, 2.0, 3.0, 4.0)
    val r2 = factory.rectangle(0.0, 0.0, 4.0, 4.0)
    val group = GroupPair.create(listOf(r1), listOf(r2))
    assertThat(group.areaSum).isEqualTo(20.0)
  }
}
