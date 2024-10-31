@file:Suppress("unused")

package com.alext.rtree.api

import com.alext.rtree.core.geometry.Circle
import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.Line
import com.alext.rtree.core.geometry.Point
import com.alext.rtree.core.geometry.Rectangle

typealias FilterFunc<A, B, C> = (a: A, b: B) -> C

object SearchFilter {

  val CIRCLE_RECTANGLE: FilterFunc<Circle, Rectangle, Boolean> = object : FilterFunc<Circle, Rectangle, Boolean> {
    override fun invoke(circle: Circle, rectangle: Rectangle): Boolean {
      return circle.intersects(rectangle)
    }
  }

  val RECTANGLE_CIRCLE: FilterFunc<Rectangle, Circle, Boolean> = object : FilterFunc<Rectangle, Circle, Boolean> {
    override fun invoke(r: Rectangle, c: Circle): Boolean {
      return CIRCLE_RECTANGLE(c, r)
    }
  }

  val CIRCLE_POINT: FilterFunc<Circle, Point, Boolean> = object : FilterFunc<Circle, Point, Boolean> {
    override fun invoke(c: Circle, p: Point): Boolean {
      return c.intersects(p)
    }
  }

  val POINT_CIRCLE: FilterFunc<Point, Circle, Boolean> = object : FilterFunc<Point, Circle, Boolean> {
    override fun invoke(p: Point, c: Circle): Boolean {
      return CIRCLE_POINT(c, p)
    }
  }

  val CIRCLE_CIRCLE: FilterFunc<Circle, Circle, Boolean> = object : FilterFunc<Circle, Circle, Boolean> {
    override fun invoke(a: Circle, b: Circle): Boolean {
      return a.intersects(b)
    }
  }

  val LINE_LINE: FilterFunc<Line, Line, Boolean> = object : FilterFunc<Line, Line, Boolean> {
    override fun invoke(a: Line, b: Line): Boolean {
      return a.intersects(b)
    }
  }

  val RECTANGLE_LINE: FilterFunc<Rectangle, Line, Boolean> = object : FilterFunc<Rectangle, Line, Boolean> {
    override fun invoke(r: Rectangle, l: Line): Boolean {
      return l.intersects(r)
    }
  }

  val LINE_RECTANGLE: FilterFunc<Line, Rectangle, Boolean> = object : FilterFunc<Line, Rectangle, Boolean> {
    override fun invoke(a: Line, r: Rectangle): Boolean {
      return RECTANGLE_LINE(r, a)
    }
  }

  val CIRCLE_LINE: FilterFunc<Circle, Line, Boolean> = object : FilterFunc<Circle, Line, Boolean> {
    override fun invoke(c: Circle, a: Line): Boolean {
      return a.intersects(c)
    }
  }

  val LINE_CIRCLE: FilterFunc<Line, Circle, Boolean> = object : FilterFunc<Line, Circle, Boolean> {
    override fun invoke(a: Line, c: Circle): Boolean {
      return CIRCLE_LINE(c, a)
    }
  }

  val POINT_LINE: FilterFunc<Point, Line, Boolean> = object : FilterFunc<Point, Line, Boolean> {

    override fun invoke(point: Point, line: Line): Boolean {
      return line.intersects(point)
    }
  }

  val LINE_POINT: FilterFunc<Line, Point, Boolean> = object : FilterFunc<Line, Point, Boolean> {
    override fun invoke(line: Line, point: Point): Boolean {
      return POINT_LINE(point, line)
    }
  }

  val GEOMETRY_LINE: FilterFunc<Geometry, Line, Boolean> = object : FilterFunc<Geometry, Line, Boolean> {
    override fun invoke(geometry: Geometry, line: Line): Boolean {
      return when (geometry) {
        is Line -> line.intersects(geometry)
        is Circle -> line.intersects(geometry)
        is Point -> line.intersects(geometry)
        is Rectangle -> line.intersects(geometry)
        else -> throw RuntimeException("unrecognized geometry: $geometry")
      }
    }
  }

  val GEOMETRY_CIRCLE: FilterFunc<Geometry, Circle, Boolean> = object : FilterFunc<Geometry, Circle, Boolean> {
    override fun invoke(geometry: Geometry, circle: Circle): Boolean {
      return when (geometry) {
        is Line -> circle.intersects(geometry)
        is Circle -> circle.intersects(geometry)
        is Point -> circle.intersects(geometry)
        is Rectangle -> circle.intersects(geometry)
        else -> throw RuntimeException("unrecognized geometry: $geometry")
      }
    }
  }

  val CIRCLE_GEOMETRY: FilterFunc<Circle, Geometry, Boolean> = object : FilterFunc<Circle, Geometry, Boolean> {
    override fun invoke(circle: Circle, geometry: Geometry): Boolean {
      return GEOMETRY_CIRCLE(geometry, circle)
    }
  }

  val GEOMETRY_RECTANGLE: FilterFunc<Geometry, Rectangle, Boolean> = object : FilterFunc<Geometry, Rectangle, Boolean> {
    override fun invoke(geometry: Geometry, r: Rectangle): Boolean {
      return when (geometry) {
        is Line -> geometry.intersects(r)
        is Circle -> geometry.intersects(r)
        is Rectangle -> r.intersects(geometry)
        else -> geometry.intersects(r)
      }
    }
  }

  val RECTANGLE_GEOMETRY: FilterFunc<Rectangle, Geometry, Boolean> = object : FilterFunc<Rectangle, Geometry, Boolean> {
    override fun invoke(r: Rectangle, geometry: Geometry): Boolean {
      return GEOMETRY_RECTANGLE(geometry, r)
    }
  }

  val GEOMETRY_POINT: FilterFunc<Geometry, Point, Boolean> = object : FilterFunc<Geometry, Point, Boolean> {
    override fun invoke(geometry: Geometry, point: Point): Boolean {
      return GEOMETRY_RECTANGLE(geometry, point.mbr)
    }
  }

  val POINT_GEOMETRY: FilterFunc<Point, Geometry, Boolean> = object : FilterFunc<Point, Geometry, Boolean> {
    override fun invoke(point: Point, geometry: Geometry): Boolean {
      return GEOMETRY_POINT(geometry, point)
    }
  }
}
