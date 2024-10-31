package com.alext.testutils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun executeConcurrently(
  times: Int,
  runnable: suspend (order: Int) -> Unit,
  onError: ((e: Exception) -> Unit)? = null,
  doAssertion: (() -> Unit)? = null
) {
  coroutineScope {
    val deferreds = mutableListOf<Deferred<Unit?>>()
    repeat(times) { index ->
      async {
        try {
          runnable.invoke(index)
        } catch (e: Exception) {
          onError?.invoke(e)
        }
      }.let {
        deferreds.add(it)
      }
    }
    deferreds.awaitAll()
  }
  doAssertion?.invoke()
}
