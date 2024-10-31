package com.alext.crypto

import org.apache.commons.lang3.RandomStringUtils
import org.mindrot.jbcrypt.BCrypt

interface PasswordManager {
  fun hash(password: String): String
  fun check(password: String, hash: String): Boolean
  fun random(length: Int = 8): String
  fun randomResetKey(length: Int = 50): String
  fun isValidPassword(password: String): Boolean
}

class DefaultPasswordManager(
  private val round: Int = 10
) : PasswordManager {

  companion object {
    private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTVWXYZabcdefghijklmnofqrstuvwxyz1234567890"
  }

  override fun hash(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt(round))
  }

  override fun check(password: String, hash: String): Boolean {
    return BCrypt.checkpw(password, hash)
  }

  override fun random(length: Int): String {
    require(length >= 1) {
      "length must be at least 1"
    }
    return (0 until length).map { CHARACTERS.random() }.joinToString("")
  }

  override fun randomResetKey(length: Int): String {
    return RandomStringUtils.randomAlphabetic(length)
  }

  /**
   * Validates a password based on the following rules:
   * - Length must be at least 8 characters.
   * - Must contain at least one uppercase letter.
   * - Must contain at least one lowercase letter.
   * - Must contain at least one special characters.
   * - Must contain at least one digit.
   * - Cannot contain spaces.
   * - Cannot consist only of special characters.
   *
   * @param password The password to be validated.
   * @return True if the password is valid, false otherwise.
   * @throws IllegalArgumentException If the password is null or empty.
   */
  override fun isValidPassword(password: String): Boolean {
    val uppercaseRegex = Regex("(?=.*[A-Z])")
    val lowercaseRegex = Regex("(?=.*[a-z])")
    val digitRegex = Regex("(?=.*\\d)")
    val spaceRegex = Regex("(?=.*\\s)")
    val specialCharRegex = Regex("(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])")

    return (
      password.length >= 8 &&
        uppercaseRegex.containsMatchIn(password) &&
        lowercaseRegex.containsMatchIn(password) &&
        digitRegex.containsMatchIn(password) &&
        specialCharRegex.containsMatchIn(password) &&
        !spaceRegex.containsMatchIn(password)
      )
  }
}
