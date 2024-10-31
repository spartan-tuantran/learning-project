package com.alext.jackson

import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer

/**
 * To handle map type of the form `Map<SmartEnum<*>, V>`
 */
internal class SmartEnumKeyDeserializer<E : Enum<E>>(
  private val enumType: JavaType? = null,
  private val rawClass: Class<*>
) : KeyDeserializer(), ContextualKeyDeserializer {

  override fun deserializeKey(key: String?, context: DeserializationContext): SmartEnum<*>? {
    return key?.let {
      try {
        @Suppress("UNCHECKED_CAST")
        val enum = java.lang.Enum.valueOf(rawClass as Class<E>, key.uppercase())
        SmartEnum(enum, key)
      } catch (e: Exception) {
        SmartEnum(null, key)
      }
    }
  }

  /**
   * Override [createContextual] to forward [JavaType] information to actual implementation
   */
  override fun createContextual(context: DeserializationContext, property: BeanProperty?): KeyDeserializer {
    val enumType = context.contextualType?.containedType(0)?.containedType(0)
    @Suppress("UNCHECKED_CAST")
    return SmartEnumKeyDeserializer(enumType, enumType?.rawClass as Class<E>)
  }
}
