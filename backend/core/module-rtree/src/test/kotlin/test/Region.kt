package test

import java.util.UUID
import org.postgis.Polygon

data class Region(
  val id: UUID = UUID.randomUUID(),
  val name: String = "hi",
  val region: Polygon = Polygon("POLYGON ((0 0,1 0,1 1,0 1,0 0))")
)
