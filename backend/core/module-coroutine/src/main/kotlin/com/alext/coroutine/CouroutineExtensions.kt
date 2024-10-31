package com.alext.coroutine

import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Retry a block of code up to [times] times, with exponential backoff.
 */
suspend fun <T> retry(
  times: Int,
  initialDelayMs: Long = 100, // 100ms
  maxDelayMs: Long = 1000, // 1s
  onError: ((Exception) -> Unit)? = null,
  block: suspend () -> T
): T? {
  var currentDelayMs = initialDelayMs
  repeat(times - 1) {
    try {
      return block()
    } catch (e: Exception) {
      onError?.invoke(e)
    }
    delay(currentDelayMs)
    currentDelayMs = (currentDelayMs * 2.0).toLong().coerceAtMost(maxDelayMs)
  }
  return runCatching { block() }.getOrNull()
}

/**
 * Execute a block of code concurrently
 *
 * @param times: How many call will run in parallel for the given [executionBlock]
 * @param executionBlock: Execution Block
 * @param exceptionBlock: During execute the [executionBlock], if any exception occurred, it will be handled here
 * @param assertionBlock: Assertion block, will be invoked after all threads done its job
 */
@OptIn(DelicateCoroutinesApi::class)
fun concurrentExecution(
  times: Int,
  executionBlock: (order: Int) -> Unit,
  exceptionBlock: (e: Exception) -> Unit = {},
  assertionBlock: () -> Unit
) {
  val latch = CountDownLatch(times)
  for (i in 0 until times) {
    GlobalScope.launch {
      try {
        executionBlock.invoke(i)
      } catch (e: Exception) {
        exceptionBlock.invoke(e)
      }
      latch.countDown()
    }
  }
  latch.await()
  assertionBlock.invoke()
}
