package com.alext.jackson

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SmartEnumModuleTest {

  private val mapper = ObjectMapper().configured()
  private val nope = ObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
    configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
    configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    findAndRegisterModules()
  }

  enum class StandardEnum {
    HELLO,
    KOTLIN;

    @JsonValue
    override fun toString(): String {
      return name.lowercase()
    }
  }

  enum class JsonPropertyEnum {
    @JsonProperty("Hello")
    HELLO,

    @JsonProperty("Kotlin")
    KOTLIN
  }

  enum class JsonCreatorEnum {
    HELLO,
    KOTLIN;

    companion object {

      @JvmStatic
      @JsonCreator
      fun from(s: String): JsonCreatorEnum? {
        return when (s) {
          "h" -> HELLO
          "k" -> KOTLIN
          else -> null
        }
      }
    }
  }

  data class Zero(
    val enum: StandardEnum
  )

  data class One(
    val enum: SmartEnum<StandardEnum>
  )

  data class Two(
    val enum: SmartEnum<JsonPropertyEnum>
  )

  data class Three(
    val enum: SmartEnum<JsonCreatorEnum>
  )

  data class CustomGenericType<T>(
    val enum: T
  )

  data class Four(
    val array: Array<SmartEnum<StandardEnum>> = arrayOf(),
    val list: List<SmartEnum<StandardEnum>> = emptyList(),
    val set: Set<SmartEnum<StandardEnum>> = setOf(),
    val enum: SmartEnum<StandardEnum>,
    val stringMap: Map<String, SmartEnum<StandardEnum>> = emptyMap(),
    val enumMap: Map<StandardEnum, SmartEnum<StandardEnum>> = emptyMap(),
    val generic: CustomGenericType<SmartEnum<StandardEnum>>
  ) {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Four) return false
      if (!array.contentEquals(other.array)) return false
      if (list != other.list) return false
      if (set != other.set) return false
      if (enum != other.enum) return false
      if (stringMap != other.stringMap) return false
      if (enumMap != other.enumMap) return false
      if (generic != other.generic) return false

      return true
    }

    override fun hashCode(): Int {
      var result = array.contentHashCode()
      result = 31 * result + list.hashCode()
      result = 31 * result + set.hashCode()
      result = 31 * result + enum.hashCode()
      result = 31 * result + stringMap.hashCode()
      result = 31 * result + enumMap.hashCode()
      result = 31 * result + generic.hashCode()
      return result
    }
  }

  data class Five(
    val map: Map<SmartEnum<StandardEnum>, StandardEnum> = emptyMap()
  )

  @Test
  fun `nested enums`() {
    val four = Four(
      array = arrayOf(com.alext.jackson.SmartEnumModuleTest.StandardEnum.HELLO.asSmartEnum(), com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum()),
      list = listOf(com.alext.jackson.SmartEnumModuleTest.StandardEnum.HELLO.asSmartEnum(), com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum()),
      set = setOf(com.alext.jackson.SmartEnumModuleTest.StandardEnum.HELLO.asSmartEnum(), com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum()),
      stringMap = mapOf(
        "hello" to com.alext.jackson.SmartEnumModuleTest.StandardEnum.HELLO.asSmartEnum(),
        "kotlin" to com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum()
      ),
      enumMap = mapOf(
        StandardEnum.HELLO to com.alext.jackson.SmartEnumModuleTest.StandardEnum.HELLO.asSmartEnum(),
        StandardEnum.KOTLIN to com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum()
      ),
      enum = com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum(),
      generic = CustomGenericType(com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum())
    )
    val json = mapper.writeValueAsString(four)
    val result = mapper.readValue<Four>(json)
    assertThat(result).isEqualTo(four)
  }

  @Test
  fun `safe enum map`() {
    val four = Five(
      map = mapOf(
        com.alext.jackson.SmartEnumModuleTest.StandardEnum.HELLO.asSmartEnum() to StandardEnum.HELLO,
        com.alext.jackson.SmartEnumModuleTest.StandardEnum.KOTLIN.asSmartEnum() to StandardEnum.KOTLIN
      )
    )
    val json = mapper.writeValueAsString(four)
    val result = mapper.readValue<Five>(json)
    assertThat(result).isEqualTo(four)
  }

  @Test
  fun `JSON should match enum`() {
    val enum = StandardEnum.HELLO
    assertThat(nope.writeValueAsString(Zero(enum)))
      .isEqualTo(mapper.writeValueAsString(One(enum = enum.asSmartEnum())))
  }

  @Test
  fun `standard enum - round trip`() {
    val expected = One(enum = SmartEnum(StandardEnum.HELLO))
    val json = mapper.writeValueAsString(expected)
    val actual = mapper.readValue<One>(json)
    assertThat(actual).isEqualTo(expected)
  }

  @Test
  fun `json property enum - round trip`() {
    val expected = Two(enum = SmartEnum(JsonPropertyEnum.HELLO, "HELLO"))
    val json = mapper.writeValueAsString(expected)
    println(json)
    val actual = mapper.readValue<Two>(json)
    assertThat(actual).isEqualTo(expected)
  }

  @Test
  fun `json property enum - parse`() {
    val json = """
      {
        "enum": "Hello"
      }
    """.trimIndent()
    val actual = mapper.readValue<Two>(json).enum
    assertThat(actual.value).isEqualTo(JsonPropertyEnum.HELLO)
    assertThat(actual.raw).isEqualTo("Hello")
  }

  @Test
  fun `json creator enum - parse`() {
    val json = """
      {
        "enum": "h"
      }
    """.trimIndent()
    val actual = mapper.readValue<Three>(json).enum
    assertThat(actual.value).isEqualTo(JsonCreatorEnum.HELLO)
    assertThat(actual.raw).isEqualTo("h")
  }

  @Test
  fun `try to parse unknown`() {
    val json = """
      {
        "enum": "three"
      }
    """.trimIndent()
    val actual = mapper.readValue<One>(json).enum
    assertThat(actual.value).isEqualTo(null)
    assertThat(actual.raw).isEqualTo("three")
  }
}
