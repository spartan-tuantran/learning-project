package com.alext.retrofit

import com.alext.logging.logger
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object Retrofits {

  private val logger = javaClass.logger()

  fun new(
    url: HttpUrl,
    jackson: ObjectMapper
  ): Retrofit {
    return newBuilder(url, jackson)
      .client(
        OkHttpClient
          .Builder()
          .addInterceptor(
            HttpLoggingInterceptor {
              logger.info(it)
            }.apply {
              level = HttpLoggingInterceptor.Level.BODY
            }
          )
          .build()
      )
      .build()
  }

  fun newBuilder(
    url: HttpUrl,
    jackson: ObjectMapper
  ): Retrofit.Builder {
    return Retrofit.Builder()
      .baseUrl(url)
      .addCallAdapterFactory(EitherCallAdapterFactory)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(JacksonConverterFactory.create(jackson))
  }
}
