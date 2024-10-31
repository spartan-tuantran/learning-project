package com.alext.rtree.extension.jts

import com.alext.rtree.core.geometry.Geometry
import java.util.UUID

/**
 * A lighter weight geometry model which only stores a [UUID] as reference to
 * the underlying geometry.
 */
interface IndexableGeometry : Geometry {
  val id: UUID
}
