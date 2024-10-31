package com.alext.rtree.renderer

import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Rectangle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.RectangleFactory

class RectangleViewTest : RectangleFactory {

  @Test
  fun `viewsBeginAt max child 1`() {
    val tree = RTree
      .builder<Int, Rectangle>()
      .minChildren(1)
      .maxChildren(1)
      .create()
      .add(1, r(1))
      .add(2, r(2))
      .add(3, r(4))
    assertThat(tree.root!!.viewsAt(0)).hasSize(9)
  }

  @Test
  fun `viewsBeginAt max child 3`() {
    val tree = RTree
      .builder<Int, Rectangle>()
      .minChildren(1)
      .maxChildren(3)
      .create()
      .add(1, r(1))
      .add(2, r(2))
      .add(3, r(4))
    assertThat(tree.root!!.viewsAt(0)).hasSize(4)
  }
}
