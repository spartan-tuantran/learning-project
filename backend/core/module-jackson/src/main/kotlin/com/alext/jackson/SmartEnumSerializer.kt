package com.alext.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException

internal class SmartEnumSerializer : JsonSerializer<SmartEnum<*>>() {

  @Throws(IOException::class)
  override fun serialize(value: SmartEnum<*>?, gen: JsonGenerator, serializers: SerializerProvider) {
    val enum = value?.value
    if (enum != null) {
      val serializer = serializers.findValueSerializer(enum.javaClass)
      if (serializer != null) {
        serializer.serialize(enum, gen, serializers)
      } else {
        gen.writeString(enum.name.lowercase())
      }
    } else {
      gen.writeString(value?.raw)
    }
  }
}
