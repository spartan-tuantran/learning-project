package com.alext.retrofit

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.lang.reflect.Type
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class EitherCall<R>(
  private val call: Call<R>,
  private val responseType: Type
) : Call<Either<ErrorResponse, R>> {

  companion object {
    private val jackson = ObjectMapper().apply {
      registerModule(
        KotlinModule
          .Builder()
          .withReflectionCacheSize(512)
          .configure(KotlinFeature.NullToEmptyCollection, false)
          .configure(KotlinFeature.NullToEmptyMap, false)
          .configure(KotlinFeature.NullIsSameAsDefault, false)
          .configure(KotlinFeature.SingletonSupport, false)
          .configure(KotlinFeature.StrictNullChecks, false)
          .build()
      )
    }
  }

  override fun enqueue(callback: Callback<Either<ErrorResponse, R>>) {
    call.enqueue(
      object : Callback<R> {
        override fun onResponse(call: Call<R>, response: Response<R>) {
          val either = if (!response.isSuccessful) {
            val error = try {
              response.errorBody().use { e ->
                jackson.readValue(e?.string(), ErrorResponse::class.java)
              }
            } catch (e: Exception) {
              ErrorResponse(e.message, response.code().toString())
            }
            Either.Left(error)
          } else {
            val body = response.body()
            if (body == null) {
              Either.Left(ErrorResponse("null", "404"))
            } else {
              Either.Right(body)
            }
          }
          callback.onResponse(this@EitherCall, Response.success(either))
        }

        override fun onFailure(call: Call<R>, throwable: Throwable) {
          val error = ErrorResponse(throwable.message, "500")
          callback.onResponse(this@EitherCall, Response.success(Either.Left(error)))
        }
      }
    )
  }

  override fun isExecuted(): Boolean {
    return call.isExecuted
  }

  override fun clone(): Call<Either<ErrorResponse, R>> {
    return EitherCall(call.clone(), responseType)
  }

  override fun isCanceled(): Boolean {
    return call.isCanceled
  }

  override fun cancel() {
    call.cancel()
  }

  override fun execute(): Response<Either<ErrorResponse, R>> {
    val response = call.execute()
    val either = if (!response.isSuccessful) {
      val error = try {
        response.errorBody().use { e ->
          jackson.readValue(e?.string(), ErrorResponse::class.java)
        }
      } catch (e: Exception) {
        ErrorResponse(e.message, response.code().toString())
      }
      Either.Left(error)
    } else {
      val body = response.body()
      if (body == null) {
        Either.Left(ErrorResponse("null", "404"))
      } else {
        Either.Right(body)
      }
    }
    return Response.success(either)
  }

  override fun request(): Request {
    return call.request()
  }

  override fun timeout(): Timeout {
    return call.timeout()
  }
}
