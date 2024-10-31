package com.alext.rtree.core

import com.alext.rtree.api.Entry
import com.alext.rtree.core.geometry.Geometries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EntryTest {

  private val factory = Geometries

  @Test
  fun `must have hashCode`() {
    val r1 = factory.rectangle(0.0, 1.0, 3.0, 5.0)
    val r2 = factory.rectangle(1.0, 2.0, 4.0, 6.0)
    val e1 = Entry.create(1, r1)
    val e2 = Entry.create(2, r2)
    val e3 = Entry.create(2, r2)
    assertThat(setOf(e1, e2, e3)).hasSize(2)
    assertThat(e2).isEqualTo(e3)
  }

  @Test
  fun `must have equals`() {
    val r1 = factory.rectangle(0.0, 1.0, 3.0, 5.0)
    val r2 = factory.rectangle(1.0, 2.0, 4.0, 6.0)
    val e1 = Entry.create(1, r1)
    val e2 = Entry.create(2, r2)
    val e3 = Entry.create(2, r2)
    assertThat(e2).isEqualTo(e3)
    assertThat(e1).isNotEqualTo(e2)
    assertThat(e1).isNotEqualTo(e3)
  }
}
