package com.alext.rtree.renderer

import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Rectangle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import test.ConsoleLogging
import test.RectangleFactory

@EnabledIfEnvironmentVariable(named = "test", matches = "local")
class TreeRendererTest : ConsoleLogging, RectangleFactory {

  @Test
  fun `render and save to file`() {
    val tree = RTree.builder<Int, Rectangle>().rstar().create()
      .add(1, r(1))
      .add(2, r(2))
      .add(3, r(4))
    // Render tree and save to file
    assertThat(tree.renderer(200, 200).saveTo("regions.png")).isEqualTo(true)
  }

  @Test
  fun `tree mbr`() {
    val tree = RTree.builder<Int, Rectangle>().rstar().create()
      .add(1, r(1))
      .add(2, r(2))
      .add(3, r(4))
    assertThat(tree.mbr()).isEqualTo(Geometries.rectangle(0.0, 0.0, 5.0, 5.0))
  }
}
