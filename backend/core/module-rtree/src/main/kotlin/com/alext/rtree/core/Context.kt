package com.alext.rtree.core

import com.alext.rtree.core.builder.AbstractBuilder
import com.alext.rtree.core.selector.Selector
import com.alext.rtree.core.splitter.Splitter

/**
 * Context to store tree configuration
 */
class Context internal constructor(builder: AbstractBuilder<*, *, *>) {
  val minChildren: Int = builder.minChildren
  val maxChildren: Int = builder.maxChildren
  val loadingFactor = builder.loadingFactor
  val selector: Selector = builder.selector
  val splitter: Splitter = builder.splitter
}
