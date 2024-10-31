package com.alext.rtree.renderer

import com.alext.rtree.api.RTree
import com.alext.rtree.api.TreeDepth
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Rectangle
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.max
import kotlin.math.roundToInt

class TreeRenderer<T, G : Geometry> internal constructor(
  width: Int,
  height: Int,
  private val tree: RTree<T, G>,
  private val view: Rectangle
) : TreeDepth, Renderer(width, height) {

  private val depth: Int = depth()

  private fun round(d: Double): Int {
    return d.roundToInt()
  }

  override fun depth(): Int {
    return depth(tree.root)
  }

  override fun draw(graphics: Graphics2D) {
    tree.root?.let { root ->
      graphics.draw(root.viewsAt(0).sortedBy { it.depth })
    }
  }

  private fun Graphics2D.draw(nodes: List<RectangleView>) {
    for (node in nodes) {
      val color = Color.getHSBColor(node.depth / (depth + 1f), 1f, 1f)
      this.stroke = BasicStroke(max(0.5f, (depth - node.depth + 1 - 1).toFloat()))
      this.color = color
      draw(node.rectangle)
    }
  }

  private fun Graphics2D.draw(rectangle: Rectangle) {
    val x1 = ((rectangle.x1 - view.x1) / (view.x2 - view.x1) * width)
    val y1 = ((rectangle.y1 - view.y1) / (view.y2 - view.y1) * height)
    val x2 = ((rectangle.x2 - view.x1) / (view.x2 - view.x1) * width)
    val y2 = ((rectangle.y2 - view.y1) / (view.y2 - view.y1) * height)
    drawRect(round(x1), round(y1), max(round(x2 - x1), 1), max(round(y2 - y1), 1))
  }

  companion object Factory {
    fun <T, G : Geometry> of(tree: RTree<T, G>, width: Int, height: Int): TreeRenderer<T, G> {
      return TreeRenderer(width, height, tree, tree.mbr())
    }
  }
}

/**
 * Return the MBR rectangle that cover all entry geometries
 */
fun <T, G : Geometry> RTree<T, G>.mbr(): Rectangle {
  return entries().fold(Geometries.rectangle(0.0, 0.0, 0.0, 0.0)) { result, entry ->
    result.merge(entry.geometry.mbr)
  }
}

/**
 * Convenient factory method to create a [TreeRenderer]
 *
 * @param width The width of the renderer
 * @param height The height of the renderer
 */
fun <T, G : Geometry> RTree<T, G>.renderer(width: Int, height: Int): TreeRenderer<T, G> {
  return TreeRenderer.of(this, width, height)
}
