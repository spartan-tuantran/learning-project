package com.alext.retrofit

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EitherCallAdapterTest {

  private val server by lazy {
    MockWebServer().apply {
      start()
    }
  }

  private val client by lazy {
    Retrofits
      .new(server.url("/coroutine/"), ObjectMapper())
      .create(EitherCoroutineClient::class.java)
  }

  @Test
  fun `either - bad request`() {
    val errorResponse = ErrorResponse("BAD REQUEST", "400")
    server.enqueue(MockResponse().setResponseCode(400).setBody(Json.encodeToString(errorResponse)))
    val response = runBlocking {
      client.nullable()
    }
    assertThat(response.swap().getOrNull()).isEqualTo(errorResponse)
  }

  @Test
  fun `either - nullable is 404`() {
    val errorResponse = ErrorResponse("NOT FOUND", "404")
    server.enqueue(MockResponse().setResponseCode(404).setBody(Json.encodeToString(errorResponse)))
    val response = runBlocking {
      client.nullable()
    }
    assertThat(response.getOrNull()).isEqualTo(null)
    assertThat(response.swap().getOrNull()).isEqualTo(errorResponse)
  }

  @Test
  fun `either - nullable 200`() {
    server.enqueue(MockResponse().setResponseCode(200))
    val response = runBlocking {
      client.nullable()
    }
    assertThat(response.getOrNull()).isEqualTo(null)
  }

  @Test
  fun `either - return scalar response`() {
    val response = "hello"
    server.enqueue(MockResponse().setResponseCode(200).setBody(response))
    val result = runBlocking {
      client.scalarResponse()
    }
    assertThat(result.getOrNull()).isEqualTo(response)
  }
}
