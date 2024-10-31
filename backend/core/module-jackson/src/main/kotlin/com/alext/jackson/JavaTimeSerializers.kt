package com.alext.jackson

import com.alext.javatime.Formatter
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class OffsetDateTimeSerializer : JsonSerializer<OffsetDateTime>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: OffsetDateTime, generator: JsonGenerator, serializers: SerializerProvider) {
    generator.writeString(Formatter.format(value))
  }
}

class OffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(parser: JsonParser, context: DeserializationContext): OffsetDateTime? {
    val text = parser.readNode().asText()
    return text?.let { Formatter.offset(text) }
  }
}

class InstantSerializer : JsonSerializer<Instant>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Instant, generator: JsonGenerator, serializers: SerializerProvider) {
    generator.writeString(Formatter.format(value))
  }
}

class InstantDeserializer : JsonDeserializer<Instant>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(parser: JsonParser, context: DeserializationContext): Instant? {
    val text = parser.readNode().asText()
    return text?.let { Formatter.instant(text) }
  }
}

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: LocalDateTime, generator: JsonGenerator, serializers: SerializerProvider) {
    generator.writeString(Formatter.format(value))
  }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDateTime? {
    val text = parser.readNode().asText()
    return text?.let { Formatter.local(text) }
  }
}

class ChronoUnitSerializer : JsonSerializer<ChronoUnit>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: ChronoUnit, generator: JsonGenerator, serializers: SerializerProvider) {
    generator.writeString(value.name.lowercase())
  }
}

class ChronoUnitDeserializer : JsonDeserializer<ChronoUnit>() {
  @Throws(IOException::class, JsonProcessingException::class, IllegalArgumentException::class)
  override fun deserialize(parser: JsonParser, context: DeserializationContext): ChronoUnit? {
    val text = parser.readNode().asText()
    return text?.let { ChronoUnit.valueOf(it.uppercase()) }
  }
}

class LocalDateSerializer : JsonSerializer<LocalDate>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: LocalDate, generator: JsonGenerator, serializers: SerializerProvider) {
    generator.writeString(Formatter.format(value))
  }
}

class LocalDateDeserializer : JsonDeserializer<LocalDate>() {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDate? {
    val text = parser.readNode().asText()
    return text?.let { Formatter.localDate(text) }
  }
}

internal fun JsonParser.readNode(): JsonNode {
  return codec.readTree(this)
}
