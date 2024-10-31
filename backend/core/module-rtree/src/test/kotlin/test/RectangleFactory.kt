package test

import com.alext.rtree.api.Entry
import com.alext.rtree.core.geometry.Geometries
import com.alext.rtree.core.geometry.Rectangle
import com.alext.rtree.extension.jts.JtsPoint
import com.alext.rtree.extension.postgis.PostgisPoint
import com.alext.rtree.misc.jts.toJtsGeometry
import java.util.Random

interface RectangleFactory {

  fun r(n: Int): Rectangle {
    return Geometries.rectangle(n.toDouble(), n.toDouble(), (n + 1).toDouble(), (n + 1).toDouble())
  }

  fun r(n: Double, m: Double): Rectangle {
    return Geometries.rectangle(n, m, n + 1, m + 1)
  }

  fun e(n: Int): Entry<Int, Rectangle> {
    return Entry.create(n, r(n))
  }

  fun e2(n: Int): Entry<Int, Rectangle> {
    return Entry.create(n, r(n - 1))
  }
}

@JvmName("postgis")
inline fun randomPoints(n: Int, crossinline f: (PostgisPoint) -> Unit) {
  randomPoints(n) { lng, lat ->
    val point = PostgisPoint(lng, lat)
    f(point)
  }
}

@JvmName("jts")
inline fun randomPoints(n: Int, crossinline f: (JtsPoint) -> Unit) {
  randomPoints(n) { lng, lat ->
    val point = PostgisPoint(lng, lat).toJtsGeometry<JtsPoint>()
    f(point)
  }
}

@JvmName("jts")
inline fun randomPoints(n: Int, crossinline f: (Double, Double) -> Unit) {
  val random = Random()
  repeat(n) {
    val lng = random.nextDouble(-180.0, +180.0)
    val lat = random.nextDouble(-90.0, +90.0)
    f(lng, lat)
  }
}
