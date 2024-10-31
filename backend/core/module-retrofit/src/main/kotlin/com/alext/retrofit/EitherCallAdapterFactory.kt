@file:Suppress("unused")

package com.alext.retrofit

import arrow.core.Either
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

object EitherCallAdapterFactory : CallAdapter.Factory() {

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    if (getRawType(returnType) != Call::class.java) {
      return null
    }

    check(returnType is ParameterizedType) {
      "Return type must be a parameterized type."
    }

    val responseType = getParameterUpperBound(0, returnType)
    if (getRawType(responseType) != Either::class.java) {
      return null
    }

    check(responseType is ParameterizedType) {
      "Response type must be a parameterized type."
    }

    val leftType = getParameterUpperBound(0, responseType)
    if (getRawType(leftType) != ErrorResponse::class.java) {
      return null
    }

    val rightType = getParameterUpperBound(1, responseType)
    return EitherCallAdapter<Any>(rightType)
  }
}
