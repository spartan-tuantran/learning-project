package com.alext.api.manager

import com.alext.client.models.response.TokenResponse
import com.alext.crypto.PasswordManager
import com.alext.exception.CustomError
import com.alext.postgresql.repository.UserRepository
import java.time.OffsetDateTime

interface AuthManager {

  fun login(
    username: String,
    password: String
  ): TokenResponse

  fun refreshToken(
    refreshToken: String
  ): TokenResponse
}

class DefaultAuthManager(
  private val userRepository: UserRepository,
  private val tokenManager: TokenManager,
  private val passwordManager: PasswordManager,
) : AuthManager {

  override fun login(
    username: String,
    password: String
  ): TokenResponse {
    val userEntity = userRepository.byUsername(username)
      ?: throw CustomError.NOT_FOUND.asException("No user found with username: $username")

    if (!passwordManager.check(password, userEntity.password)) {
      throw CustomError.BAD_REQUEST.asException("Wrong password")
    }

    val token = tokenManager.generateToken(userEntity)

    userRepository.updateLastLogin(
      userId = userEntity.id,
      lastLoginAt = OffsetDateTime.now(),
    )

    return token
  }

  override fun refreshToken(refreshToken: String): TokenResponse {
    return tokenManager.refreshToken(refreshToken)
  }
}
