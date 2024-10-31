package com.alext.redis.core.redisson

import com.alext.jackson.configured
import com.alext.redis.config.RedisClusterServerConfig
import com.alext.redis.core.Keyspace
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagedRedissonTest {

  enum class TestKeyspace : Keyspace<TestKeyspace> {
    SPARTAN
  }

  internal data class Dummy(
    val name: String
  )

  private lateinit var jackson: ObjectMapper
  private lateinit var redis: ManagedRedisson

  @BeforeAll
  fun beforeAll() {
    jackson = spyk(ObjectMapper().configured())

    redis = spyk(
      ManagedRedisson.create(
        client = RedissonFactory.cluster(
          RedisClusterServerConfig(
            hosts = System.getenv("REDIS_HOSTS") ?: RedisClusterServerConfig.DEFAULT_HOSTS,
            user = null,
            replicaHosts = null,
            password = null,
            ssl = false,
            timeout = RedisClusterServerConfig.DEFAULT_TIMEOUT,
            port = RedisClusterServerConfig.DEFAULT_PORT
          )
        ),
        namespace = TestKeyspace.SPARTAN,
        jackson = jackson
      )
    )
  }

  @AfterEach
  fun afterEach() {
    clearMocks(jackson, redis)
  }

  @Test
  fun `set and get`() {
    val key = RandomStringUtils.randomAlphanumeric(5)
    val value = RandomStringUtils.random(5)

    redis.set(key, value)
    assertThat(redis.get(key, String::class.java)).isEqualTo(value)
    assertThat(redis.get(RandomStringUtils.randomAlphanumeric(5), String::class.java)).isEqualTo(null)
  }

  @Test
  fun `set, get and delete`() {
    val key = RandomStringUtils.randomAlphanumeric(5)
    val value = RandomStringUtils.random(5)

    redis.set(key, value)
    assertThat(redis.get(key, String::class.java)).isEqualTo(value)
    assertThat(redis.delete(key)).isEqualTo(true)
    assertThat(redis.get(key, String::class.java)).isEqualTo(null)
  }

  @Test
  fun `set with expiration`() {
    val key = RandomStringUtils.randomAlphanumeric(5)
    val value = RandomStringUtils.random(5)

    redis.set(key, value, 1)
    Thread.sleep(1100)
    assertThat(redis.get(key, String::class.java)).isEqualTo(null)
  }

  @Test
  fun `guard exception`() {
    val result = redis.guard {
      "123".repeat(1)[100]
      1
    }
    assertThat(result).isEqualTo(null)
  }

  @Test
  fun `cached item which is failed to parse should be removed from caching`() {
    every { jackson.writeValueAsString(any()) } returns "\\u333{\"name\":\"zzz\"}"

    val key = RandomStringUtils.randomAlphanumeric(5)
    redis.set(key, Dummy(RandomStringUtils.random(5)))

    val item = redis.get(key, Dummy::class.java)
    assertThat(item).isEqualTo(null)

    verify(exactly = 1) {
      redis.delete(key)
    }

    clearMocks(jackson)
  }
}
