package com.alext.redis.lock

import com.alext.coroutine.concurrentExecution
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import test.AbstractRedisTest

abstract class DistributedLockServiceTest : AbstractRedisTest() {

  abstract val lockService: DistributedLockService

  @Test
  fun `only one thread can acquire lock of given key at a time`() {
    val successCount = AtomicInteger(0)
    val failureCount = AtomicInteger(0)
    val key = UUID.randomUUID()

    concurrentExecution(
      times = 10,
      executionBlock = {
        lockService.acquire(
          key = key,
          waitTimeMillis = 0,
          leaseTimeMillis = 10_000,
          onSuccess = {
            successCount.incrementAndGet()
            // Simulate that the task take 5 secs to run
            Thread.sleep(5_000)
          }
        ) {
          failureCount.incrementAndGet()
        }
      },
      assertionBlock = {
        assertThat(successCount.get()).isEqualTo(1)
        assertThat(failureCount.get()).isEqualTo(9)
      }
    )
  }

  @Test
  fun `lock should be released after leasing time`() {
    val successCount = AtomicInteger(0)
    val failureCount = AtomicInteger(0)
    val executors = Executors.newFixedThreadPool(3)
    val latch = CountDownLatch(3)
    val key = UUID.randomUUID()

    // First, acquiring the lock for 5 seconds
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 5_000,
        onSuccess = {
          successCount.incrementAndGet()
          // After 3 seconds, task done
          Thread.sleep(3_000)
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    // After 1 second, someone try to acquire
    Thread.sleep(1_000)

    // Because the lock still be hold by first call, so they can not acquire the lock
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 3_000,
        onSuccess = {
          successCount.incrementAndGet()
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    // After 4.5 seconds more, now the lock has been released
    Thread.sleep(4_500)

    // Acquire again, we are good to go now
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 3_000,
        onSuccess = {
          successCount.incrementAndGet()
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    latch.await()

    // So success acquiring should be 2
    assertThat(successCount.get()).isEqualTo(2)
    // And just one failure
    assertThat(failureCount.get()).isEqualTo(1)
  }

  @Test
  fun `lock should be released after done task`() {
    val successCount = AtomicInteger(0)
    val failureCount = AtomicInteger(0)
    val executors = Executors.newFixedThreadPool(3)
    val latch = CountDownLatch(3)
    val key = UUID.randomUUID()

    // First, acquiring the lock for 5 seconds
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 5_000,
        onSuccess = {
          successCount.incrementAndGet()
          // After 3 seconds, the task done
          Thread.sleep(3_000)
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    // After 1 second, someone try to acquire
    Thread.sleep(1_000)

    // Because the lock still be hold by first call, so they can not acquire the lock
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 3_000,
        onSuccess = {
          successCount.incrementAndGet()
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    // After 3 seconds more, now the lock has been released because task just done
    Thread.sleep(3_000)

    // Acquire again, we are good to go now
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 3_000,
        onSuccess = {
          successCount.incrementAndGet()
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    latch.await()

    // So success acquiring should be 2
    assertThat(successCount.get()).isEqualTo(2)
    // And just one failure
    assertThat(failureCount.get()).isEqualTo(1)
  }

  @Test
  fun `lock should be released if exception happens`() {
    val successCount = AtomicInteger(0)
    val failureCount = AtomicInteger(0)
    val executors = Executors.newFixedThreadPool(3)
    val latch = CountDownLatch(3)
    val key = UUID.randomUUID()

    // First, acquiring the lock for 5 seconds
    executors.execute {
      catchThrowable {
        lockService.acquire(
          key = key,
          waitTimeMillis = 0,
          leaseTimeMillis = 5_000,
          onSuccess = {
            successCount.incrementAndGet()
            // After 3 seconds, the task throw exception
            Thread.sleep(3_000)
            latch.countDown()
            throw Throwable("oops")
          }
        ) {
          failureCount.incrementAndGet()
          latch.countDown()
        }
      }.let {
        assertThat(it.message).isEqualTo("oops")
      }
    }

    // After 1 second, someone try to acquire
    Thread.sleep(1_000)

    // Because the lock still be hold by first call, so they can not acquire the lock
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 3_000,
        onSuccess = {
          successCount.incrementAndGet()
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    // After 3 seconds more, now the lock has been released because first task throw exception
    Thread.sleep(3_000)

    // Acquire again, we are good to go now
    executors.execute {
      lockService.acquire(
        key = key,
        waitTimeMillis = 0,
        leaseTimeMillis = 3_000,
        onSuccess = {
          successCount.incrementAndGet()
          latch.countDown()
        }
      ) {
        failureCount.incrementAndGet()
        latch.countDown()
      }
    }

    latch.await()

    // So success acquiring should be 2
    assertThat(successCount.get()).isEqualTo(2)
    // And just one failure
    assertThat(failureCount.get()).isEqualTo(1)
  }
}
