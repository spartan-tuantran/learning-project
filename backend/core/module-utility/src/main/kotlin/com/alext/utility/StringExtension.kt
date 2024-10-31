package com.alext.utility

/**
 * Utility to convert String with trim() and lowercase() if is not blank.
 *
 * @return The null|string that has been truncated and to lowercase.
 */
fun String?.takeIfSearchable(): String? {
  return this?.trim()?.takeIf { it.isNotEmpty() }?.lowercase()
}

/**
 * Utility to convert String with trim() and lowercase() if is not blank.
 *
 * @Return The block with string that has been truncated and to lowercase.
 */
fun String?.doWithSearch(block: (String) -> Unit) {
  this?.takeIfSearchable()?.also {
    block(it)
  }
}

/**
 * Utility to convert String with trim() and lowercase() if is not blank.
 *
 * @Return The block with string that has been truncated and to lowercase.
 * Finally, wrap a pattern like "%it%".
 */
fun String?.doWithSearchPattern(block: (String) -> Unit) {
  this?.takeIfSearchable()?.also {
    block("%$it%")
  }
}

/**
 * Utility to convert String with trim() and lowercase() if is not blank.
 *
 * @Return The block with string that has been truncated and to lowercase.
 * Finally, wrap a pattern like "%it".
 */
fun String?.doWithSuffixSearchPattern(block: (String) -> Unit) {
  this?.takeIfSearchable()?.also {
    block("%$it")
  }
}
