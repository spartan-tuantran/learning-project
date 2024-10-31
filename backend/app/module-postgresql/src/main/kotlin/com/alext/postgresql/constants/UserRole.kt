package com.alext.postgresql.constants

enum class UserRole (private val level: Int) {
  USER(0),
  ADMIN(1);

  val roles: List<String>
    get() {
      val maxLevel = level
      val roles = ALL.filter { this == it || (it.level <= maxLevel) }
      return roles.map { it.toString() }
    }

  companion object {
    private val ALL = entries.toTypedArray()
  }
}
