package com.alext.rtree.core

import com.alext.rtree.api.Node
import com.alext.rtree.api.RTree
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.GroupPair
import com.alext.rtree.core.geometry.HasGeometry
import com.alext.rtree.core.selector.Selector
import com.alext.rtree.core.splitter.Splitter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContextTest {

  @Test
  fun `builder should set correct values`() {
    val context = RTree
      .builder<Int, Geometry>()
      .minChildren(1)
      .maxChildren(4)
      .loadingFactor(2.0)
      .selector(MySelector())
      .splitter(MySplitter())
      .context()
    assertThat(context.minChildren).isEqualTo(1)
    assertThat(context.maxChildren).isEqualTo(4)
    assertThat(context.loadingFactor).isEqualTo(2.0)
    assertThat(context.splitter).isInstanceOf(MySplitter::class.java)
    assertThat(context.selector).isInstanceOf(MySelector::class.java)
  }
}

class MySelector : Selector {

  override fun <T, S : Geometry> select(geometry: Geometry, nodes: List<Node<T, S>>): Node<T, S> {
    throw UnsupportedOperationException()
  }
}

class MySplitter : Splitter {

  override fun <T : HasGeometry> split(items: List<T>, minSize: Int): GroupPair<T> {
    throw UnsupportedOperationException()
  }
}
