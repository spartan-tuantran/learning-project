package com.alext.testutils

import org.awaitility.Awaitility.await
import org.awaitility.core.ThrowingRunnable
import java.util.concurrent.TimeUnit

internal object AssertionHelper {

  /**
   * eventually evaluates given assertion
   * @param timeout await at most timeout before throwing a timeout exception
   * @param pollDelay delay before the first poll
   * @param pollInterval interval between polls
   * @param assertion assertion to evaluate
   */
  fun eventually(
    timeout: Long = 1000,
    pollDelay: Long = 0,
    pollInterval: Long = 200,
    assertion: ThrowingRunnable
  ) {
    await().atMost(timeout, TimeUnit.MILLISECONDS)
      .with()
      .pollDelay(pollDelay, TimeUnit.MILLISECONDS)
      .and()
      .pollInterval(pollInterval, TimeUnit.MILLISECONDS)
      .untilAsserted(assertion)
  }
}
