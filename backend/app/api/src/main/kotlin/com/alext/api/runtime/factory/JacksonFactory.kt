package com.alext.api.runtime.factory

import com.alext.jackson.configured
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.jackson.JacksonConfiguration
import io.micronaut.jackson.ObjectMapperFactory
import jakarta.inject.Singleton

@Factory
@Replaces(JsonFactory::class)
internal class JacksonFactory : ObjectMapperFactory() {

  @Singleton
  @Replaces(ObjectMapper::class)
  override fun objectMapper(jacksonConfiguration: JacksonConfiguration?, jsonFactory: JsonFactory?): ObjectMapper {
    return super.objectMapper(jacksonConfiguration, jsonFactory).configured()
  }
}
