package com.alext.utility

private val uppercaseLetters = ('A'..'Z')
private val lowercaseLetters = ('a'..'z')
private val digits = ('0'..'9')
private val specialChars = listOf('!', '@', '#', '$', '%', '^', '&', '*')
private val allowedChars = uppercaseLetters + lowercaseLetters + digits + specialChars

fun randomPassword(): String {
  var password: String
  while (true) {
    password = (1..8).map { allowedChars.random() }.joinToString("")
    if (password.any { it in uppercaseLetters } &&
      password.any { it in lowercaseLetters } &&
      password.any { it in digits } &&
      password.any { it in specialChars }
    ) {
      break
    }
  }
  return password
}

fun formAddress(
  addressLine1: String,
  addressLine2: String? = null,
  city: String,
  province: String,
  zip: String,
  country: String
): String {
  return buildString {
    append(addressLine1)

    if (!addressLine2.isNullOrEmpty()) {
      append(", ")
      append(addressLine2)
    }

    append(", ")
    append(city)
    append(", ")
    append(province)
    append(" ")
    append(zip)
    append(", ")
    append(country)
  }
}

fun formName(
  firstName: String? = null,
  lastName: String? = null,
  defaultName: String? = null
): String {
  return if (firstName == null && lastName == null && defaultName != null) {
    defaultName
  } else {
    "${firstName ?: ""} ${lastName ?: ""}".trim()
  }
}

fun String.sanitize(): String {
  return lowercase().trim()
}
