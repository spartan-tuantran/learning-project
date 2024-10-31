package com.alext.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import java.io.IOException

/**
 * To handle all property that use [SmartEnum] directly or indirectly via generic
 */
internal class SmartEnumDeserializer<E : Enum<E>>(
  private val enumType: JavaType? = null
) : JsonDeserializer<SmartEnum<E>>(), ContextualDeserializer {

  @Throws(JsonMappingException::class)
  override fun createContextual(context: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
    return SmartEnumDeserializer<E>(context.contextualType.containedType(0))
  }

  @Throws(IOException::class)
  override fun deserialize(parser: JsonParser, context: DeserializationContext): SmartEnum<E>? {
    return try {
      val enum = context.readValue(parser, enumType) as? E
      SmartEnum(enum, parser.text()!!)
    } catch (e: Exception) {
      // Fallback using raw value from JSON as uppercase
      parser.text()?.let { raw ->
        SmartEnum(null, raw)
      }
    }
  }

  private fun JsonParser.text(): String? {
    return codec.readTree<JsonNode>(this)?.asText()
  }
}
