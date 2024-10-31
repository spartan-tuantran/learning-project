package com.alext.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserExtensionTest {

  private val entity = UserEntityTest(
    firstName = "John",
    lastName = "Doe",
    addressLine1 = "10801 National Blvd",
    addressLine2 = "Suite 123",
    city = "Los Angeles",
    province = "CA",
    zip = "90064",
    country = "US"
  )

  @Test
  fun `address line 2 is not null`() {
    val expected = "10801 National Blvd, Suite 123, Los Angeles, CA 90064, US"
    assertThat(entity.address()).isEqualTo(expected)
  }

  @Test
  fun `address line 2 is null`() {
    val expected = "10801 National Blvd, Los Angeles, CA 90064, US"
    assertThat(entity.copy(addressLine2 = null).address()).isEqualTo(expected)
  }

  @Test
  fun `firstName and lastName are not null`() {
    assertThat(entity.name()).isEqualTo("John Doe")
  }

  @Test
  fun `firstName is null`() {
    assertThat(entity.copy(firstName = null).name()).isEqualTo("Doe")
  }

  @Test
  fun `lastName is null`() {
    assertThat(entity.copy(lastName = null).name()).isEqualTo("John")
  }

  @Test
  fun `defaultName is not null`() {
    val defaultName = "John"
    assertThat(entity.copy(firstName = null, lastName = null, userName = defaultName).name()).isEqualTo(defaultName)
  }
}

data class UserEntityTest(
  val firstName: String? = null,
  val lastName: String? = null,
  val userName: String? = null,
  val addressLine1: String,
  val addressLine2: String? = null,
  val city: String,
  val province: String,
  val zip: String,
  val country: String
) {
  fun address() = formAddress(addressLine1, addressLine2, city, province, zip, country)

  fun name() = formName(firstName, lastName, userName)
}
