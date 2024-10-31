package test

import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry
import com.alext.rtree.core.geometry.Rectangle

class Mbr(val r: Rectangle) : HasGeometry {

  override val geometry: Geometry = r
}
