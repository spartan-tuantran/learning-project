package com.alext.rtree.core.splitter

import com.alext.rtree.core.geometry.Geometries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import test.Mbr

class QuadraticSplitterTest {

  private val factory = Geometries
  private val splitter = QuadraticSplitter()

  private fun Int.mbr(): Mbr {
    val n = this.toDouble()
    return Mbr(factory.rectangle(n, n, n + 1, n + 1))
  }

  @Test
  fun `worst combination of 2`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val (first, second) = splitter.run {
      listOf(r1, r2).worstCombination()
    }
    assertThat(r1).isEqualTo(first)
    assertThat(r2).isEqualTo(second)
  }

  @Test
  fun `worst combination of 3`() {
    val r1 = 1.mbr()
    val r2 = 100.mbr()
    val r3 = 3.mbr()
    val (first, second) = splitter.run {
      listOf(r1, r2, r3).worstCombination()
    }
    assertThat(r1).isEqualTo(first)
    assertThat(r2).isEqualTo(second)
  }

  @Test
  fun `worst combination of 4`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val r3 = 3.mbr()
    val r4 = 4.mbr()
    val (first, second) = splitter.run {
      listOf(r1, r2, r3, r4).worstCombination()
    }
    assertThat(r1).isEqualTo(first)
    assertThat(r4).isEqualTo(second)
  }

  @Test
  fun `best candidate for group 1`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val list = listOf(r1)
    val group = listOf(r2)
    val r = splitter.run {
      list.bestCandidate(factory.mbr(group))
    }
    assertThat(r1).isEqualTo(r)
  }

  @Test
  fun `best candidate for group 2`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val r3 = 10.mbr()
    val list = listOf(r1)
    val group = listOf(r2, r3)
    val r = splitter.run {
      list.bestCandidate(factory.mbr(group))
    }
    assertThat(r1).isEqualTo(r)
  }

  @Test
  fun `best candidate for group 3`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val r3 = 10.mbr()
    val list = listOf(r1, r2)
    val group = listOf(r3)
    val r = splitter.run {
      list.bestCandidate(factory.mbr(group))
    }
    assertThat(r2).isEqualTo(r)
  }

  @Test
  fun `split into 2 case 1`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val r3 = 100.mbr()
    val r4 = 101.mbr()
    val pair = splitter.split(listOf(r1, r2, r3, r4), 2)
    assertThat(setOf(r1, r2)).isEqualTo(pair.first.items.toSet())
    assertThat(setOf(r3, r4)).isEqualTo(pair.second.items.toSet())
  }

  @Test
  fun `split into 2 case 2`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val r3 = 100.mbr()
    val r4 = 101.mbr()
    val r5 = 103.mbr()
    val pair = splitter.split(listOf(r1, r2, r3, r4, r5), 2)
    assertThat(setOf(r1, r2)).isEqualTo(pair.first.items.toSet())
    assertThat(setOf(r3, r4, r5)).isEqualTo(pair.second.items.toSet())
  }

  @Test
  fun `split into 3`() {
    val r1 = 1.mbr()
    val r2 = 2.mbr()
    val r3 = 100.mbr()
    val r4 = 101.mbr()
    val r5 = 103.mbr()
    val r6 = 104.mbr()
    val pair = splitter.split(listOf(r1, r2, r3, r4, r5, r6), 3)
    assertThat(setOf(r1, r2, r3)).isEqualTo(pair.first.items.toSet())
    assertThat(setOf(r4, r5, r6)).isEqualTo(pair.second.items.toSet())
  }

  @Test
  fun `cannot split empty list`() {
    assertThrows<RuntimeException> { splitter.split(emptyList(), 3) }
  }
}
