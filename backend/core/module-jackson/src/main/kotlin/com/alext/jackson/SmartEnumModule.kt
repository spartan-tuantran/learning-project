package com.alext.jackson

import com.fasterxml.jackson.databind.module.SimpleModule

object SmartEnumModule : SimpleModule() {

  init {
    addDeserializer(SmartEnum::class.java, SmartEnumDeserializer())
    addSerializer(SmartEnum::class.java, SmartEnumSerializer())
    addKeyDeserializer(SmartEnum::class.java, SmartEnumKeyDeserializer(enumType = null, rawClass = Enum::class.java))
  }
}
