package com.alext.rtree.misc

fun <T> List<T>.add(element: T): List<T> {
  val result = ArrayList<T>(size + 2)
  result.addAll(this)
  result.add(element)
  return result
}

fun <T> List<T>.remove(elements: List<T>): List<T> {
  val result = ArrayList(this)
  result.removeAll(elements)
  return result
}

fun <T> List<T>.replace(element: T, replacements: List<T>): List<T> {
  val result = ArrayList<T>(size + replacements.size)
  for (node in this) {
    if (node !== element) {
      result.add(node)
    }
  }
  result.addAll(replacements)
  return result
}
