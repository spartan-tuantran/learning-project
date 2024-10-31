package com.alext.retrofit

import arrow.core.Either
import retrofit2.http.GET
import retrofit2.http.Query

interface EitherCoroutineClient {

  @GET("/coroutine/bad-request")
  suspend fun badRequest(
    @Query("error") error: String
  ): Either<ErrorResponse, User>

  @GET("/coroutine/nullable")
  suspend fun nullable(): Either<ErrorResponse, User>

  @GET("/coroutine/error-json")
  suspend fun errorJson(
    @Query("error") error: String
  ): Either<ErrorResponse, User>

  @GET("/coroutine/scalar-response")
  suspend fun scalarResponse(): Either<ErrorResponse, String>
}
