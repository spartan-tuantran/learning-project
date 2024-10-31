package com.alext.rtree.misc.quadtree

import java.util.concurrent.ThreadLocalRandom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.ConsoleLogging

class QuadTreeTest : ConsoleLogging {
  private val bound = Bound(
    minX = -10.0,
    maxX = 10.0,
    minY = -10.0,
    maxY = 10.0
  )

  private val tree = QuadTree<Element<Int>>(bound)

  @Test
  fun `default element`() {
    assertThat(Element(value = 1).point.x).isEqualTo(0.0)
    assertThat(Element(value = 1).point.y).isEqualTo(0.0)
  }

  @Test
  fun `add into tree`() {
    with(tree) {
      add(Element(Point(0.0, 0.0), 1))
      add(Element(Point(1.0, 1.0), 2))
      add(Element(Point(2.0, 2.0), 3))
    }
    val result = tree.search(
      Bound(
        minX = 0.0,
        maxX = 3.0,
        minY = 0.0,
        maxY = 3.0
      )
    )
    assertThat(result).hasSize(3)
  }

  @Test
  fun `add then remove tree`() {
    with(tree) {
      add(Element(Point(0.0, 0.0), 1))
      add(Element(Point(1.0, 1.0), 2))
      add(Element(Point(2.0, 2.0), 3))
      remove(Element(Point(2.0, 2.0), 3))
    }
    val result = tree.search(
      Bound(
        minX = 0.0,
        maxX = 3.0,
        minY = 0.0,
        maxY = 3.0
      )
    )
    assertThat(result).hasSize(2)
  }

  @Test
  fun `remove not found`() {
    with(tree) {
      add(Element(Point(0.0, 0.0), 1))
      add(Element(Point(1.0, 1.0), 2))
      add(Element(Point(2.0, 2.0), 3))
    }
    assertThat(tree.remove(Element(Point(-1.0, -1.0), 3))).isEqualTo(false)
  }

  @Test
  fun `add out of bound`() {
    assertThat(tree.add(Element(Point(100.0, 100.0), 3))).isEqualTo(false)
  }

  @Test
  fun `remove out of bound`() {
    assertThat(tree.remove(Element(Point(100.0, 100.0), 3))).isEqualTo(false)
  }

  @Test
  fun `clear all tree`() {
    with(tree) {
      add(Element(Point(0.0, 0.0), 1))
      add(Element(Point(1.0, 1.0), 2))
      add(Element(Point(2.0, 2.0), 3))
      clear()
    }
    val result = tree.search(
      Bound(
        minX = 0.0,
        maxX = 3.0,
        minY = 0.0,
        maxY = 3.0
      )
    )
    assertThat(result).isEmpty()
    assertThat(tree.size()).isEqualTo(0)
  }

  @Test
  fun `test correctness against brute-force`() {
    val quad = QuadTree<Element<Int>>(Bound.WORLD)
    val brute = BruteForce()
    val min = 0.0
    val max = 1000.0
    val random = ThreadLocalRandom.current()
    val count = 100
    val unique = mutableSetOf<Point>()
    (1..count).forEach {
      val x = random.nextDouble(min, max)
      val y = random.nextDouble(min, max)
      val point = Point(x, y)
      if (!unique.contains(point)) {
        unique.add(point)
        val element = Element(point, it)
        quad.add(element)
        brute.add(element)
      }
    }
    blue("quad size: ${quad.size()}\n")
    blue("brute size: ${brute.elements.size}\n")
    repeat(100) {
      val start = random.nextDouble(min / 2.0, max / 2.0)
      val end = start + random.nextDouble(min / 2.0, max / 2.0)
      val bound = Bound(
        minX = start,
        maxX = end,
        minY = start,
        maxY = end
      )
      yellow("bound: $bound")
      val a = quad.search(bound).also {
        green("QuadTree report: ${it.size}")
      }
      val b = brute.search(bound).also {
        purple("BruteForce report: ${it.size}")
      }
      assertThat(a.size).isEqualTo(b.size)
    }
  }
}

class BruteForce {
  val elements = ArrayList<Element<Int>>()

  fun remove(e: Element<Int>) {
    elements.removeIf { it.point == e.point }
  }

  fun add(e: Element<Int>) {
    elements.add(e)
  }

  fun search(bound: Bound): List<Element<Int>> {
    val result = mutableListOf<Element<Int>>()
    elements.forEach {
      if (bound.contains(it.point)) {
        result.add(it)
      }
    }
    return result
  }
}
