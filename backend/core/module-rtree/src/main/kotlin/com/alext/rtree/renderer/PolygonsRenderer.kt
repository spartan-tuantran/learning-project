package com.alext.rtree.renderer

import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.extension.jts.mbr
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.util.Random
import org.locationtech.jts.geom.Polygon

typealias AwtPolygon = java.awt.Polygon

class PolygonsRenderer private constructor(
  width: Int,
  height: Int,
  private val polygons: List<Polygon>
) : Renderer(width, height) {

  private val random = Random()

  override fun draw(graphics: Graphics2D) {
    val stroke = BasicStroke(0.5f)
    val view = Geometries.mbr(polygons)
    polygons.forEach { p ->
      val polygon = AwtPolygon()
      val coordinates = p.coordinates
      for (element in coordinates) {
        val x = ((element.x - view.x1) / (view.x2 - view.x1) * width)
        val y = ((element.y - view.y1) / (view.y2 - view.y1) * height)
        polygon.addPoint(x.toInt(), y.toInt())
      }
      val color = Color.getHSBColor(random.nextFloat(), 1.0f, 1.0f)
      graphics.stroke = stroke
      graphics.color = color
      graphics.draw(polygon)
    }
  }

  companion object {
    /**
     * Create renderer for a list of polygons
     *
     * @param width The width of the renderer
     * @param height The height of the renderer
     * @param polygons The list of polygons
     */
    fun of(width: Int, height: Int, polygons: List<Polygon>): PolygonsRenderer {
      return PolygonsRenderer(width, height, polygons)
    }
  }
}

fun List<Polygon>.renderer(width: Int, height: Int) = PolygonsRenderer.of(width, height, this)
