package com.alext.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnumExtensionTest {

  enum class Gender {
    MALE,
    FEMALE
  }

  @Test
  fun `parse unknown should fallback to default`() {
    assertThat("male".asEnum<Gender>()).isEqualTo(Gender.MALE)
    assertThat("female".asEnum<Gender>()).isEqualTo(Gender.FEMALE)
    assertThat("yo".asEnum<Gender>()).isEqualTo(null)
  }
}
