package com.alext.rtree.api

import com.alext.rtree.core.geometry.Geometry
import com.alext.rtree.core.geometry.HasGeometry

/**
 * An entry that contains a value [T] and a geometry [G].
 * This is similar to [Map] entry key value pair.
 * - The geometry is used as key for indexing.
 * - The value is used for data.
 */
interface Entry<T, G : Geometry> : HasGeometry {

  /** The value of this entry. Usually user data */
  val value: T

  /** The geometry of this entry. Used by Rtree */
  override val geometry: G

  companion object Factory {

    fun <T, G : Geometry> create(value: T, geometry: G): Entry<T, G> {
      return object : Entry<T, G> {
        override val value: T = value
        override val geometry: G = geometry

        override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Entry<*, *>) return false
          if (value != other.value) return false
          if (geometry != other.geometry) return false
          return true
        }

        override fun hashCode(): Int {
          var result = value.hashCode()
          result = 31 * result + geometry.hashCode()
          return result
        }

        override fun toString(): String {
          return "Entry[value=$value, geometry=$geometry]"
        }
      }
    }
  }
}
