package com.alext.retrofit

import arrow.core.Either
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter

internal class EitherCallAdapter<R>(
  private val responseType: Type
) : CallAdapter<R, Call<Either<ErrorResponse, R>>> {

  override fun adapt(call: Call<R>): Call<Either<ErrorResponse, R>> {
    return EitherCall(call, responseType)
  }

  override fun responseType(): Type {
    return responseType
  }
}
