package com.alext.redis.core.redisson

import com.alext.redis.config.RedisClusterServerConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.client.codec.StringCodec

class RedissonFactoryTest {

  @Test
  fun `redis default config`() {
    RedissonFactory.cluster(
      RedisClusterServerConfig(
        hosts = "",
        replicaHosts = "",
        user = "",
        password = "",
        ssl = false
      )
    )

    verify(exactly = 1) {
      Redisson.create(
        withArg {
          assertThat(it.codec).isExactlyInstanceOf(StringCodec::class.java)
          val useClusterServers = it.useClusterServers()

          assertThat(useClusterServers.username).isNull()
          assertThat(useClusterServers.password).isNull()
          assertThat(useClusterServers.connectTimeout).isEqualTo(RedisClusterServerConfig.DEFAULT_TIMEOUT)

          val defaultRedisHosts = RedisClusterServerConfig.DEFAULT_HOSTS.split(",")
          val nodeAddresses = useClusterServers.nodeAddresses
          assertThat(nodeAddresses).hasSize(defaultRedisHosts.size)

          for (i in defaultRedisHosts.indices) {
            assertThat(nodeAddresses[i]).isEqualTo("redis://${defaultRedisHosts[i]}")
          }
        }
      )
    }
  }

  @Test
  fun `redis host without port`() {
    val user = RandomStringUtils.randomAlphanumeric(5)
    val password = RandomStringUtils.randomAlphanumeric(5)

    RedissonFactory.cluster(
      RedisClusterServerConfig(
        hosts = "localhost1,localhost2",
        replicaHosts = "",
        user = user,
        password = password,
        ssl = false,
        timeout = 0,
        port = 666
      )
    )

    verify(exactly = 1) {
      Redisson.create(
        withArg {
          val useClusterServers = it.useClusterServers()
          assertThat(useClusterServers.username).isEqualTo(user)
          assertThat(useClusterServers.password).isEqualTo(password)
          assertThat(useClusterServers.connectTimeout).isEqualTo(RedisClusterServerConfig.DEFAULT_TIMEOUT)

          val nodeAddresses = useClusterServers.nodeAddresses
          assertThat(nodeAddresses[0]).isEqualTo("redis://localhost1:666")
          assertThat(nodeAddresses[1]).isEqualTo("redis://localhost2:666")
        }
      )
    }
  }

  @Test
  fun `redis hosts with port`() {
    mockkStatic(Redisson::class)
    every { Redisson.create(any()) } returns mockk<Redisson>(relaxed = true)

    RedissonFactory.cluster(
      RedisClusterServerConfig(
        hosts = "localhost1:777, localhost2",
        user = "",
        replicaHosts = "",
        password = "",
        ssl = false,
        timeout = 500,
        port = 666
      )
    )

    verify(exactly = 1) {
      Redisson.create(
        withArg {
          val useClusterServers = it.useClusterServers()
          assertThat(useClusterServers.connectTimeout).isEqualTo(500)

          val nodeAddresses = useClusterServers.nodeAddresses
          assertThat(nodeAddresses[0]).isEqualTo("redis://localhost1:777")
          assertThat(nodeAddresses[1]).isEqualTo("redis://localhost2:666")
        }
      )
    }

    val configSlot = slot<org.redisson.config.Config>()
    verify(exactly = 1) {
      Redisson.create(capture(configSlot))
    }
  }

  companion object {
    @JvmStatic
    @BeforeAll
    fun beforeAll() {
      mockkStatic(Redisson::class)
      every { Redisson.create(any()) } returns mockk<Redisson>(relaxed = true)
    }

    @JvmStatic
    @AfterAll
    fun afterAll() {
      unmockkStatic(Redisson::class)
    }
  }
}
