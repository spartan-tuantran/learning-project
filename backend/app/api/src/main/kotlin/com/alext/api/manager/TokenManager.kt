package com.alext.api.manager

import com.alext.client.models.response.TokenResponse
import com.alext.exception.CustomError
import com.alext.postgresql.entity.UserEntity
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.generator.AccessRefreshTokenGenerator
import io.micronaut.security.token.refresh.RefreshTokenPersistence
import io.micronaut.security.token.validator.RefreshTokenValidator
import kotlin.jvm.optionals.getOrElse
import reactor.core.publisher.Mono

interface TokenManager {

  fun generateToken(userEntity: UserEntity): TokenResponse

  fun refreshToken(refreshToken: String): TokenResponse
}

class DefaultTokenManager(
  private val tokenGenerator: AccessRefreshTokenGenerator,
  private val refreshTokenValidator: RefreshTokenValidator,
  private val refreshTokenPersistence: RefreshTokenPersistence
) : TokenManager {

  override fun generateToken(userEntity: UserEntity): TokenResponse {
    val token = tokenGenerator.generate(
      Authentication.build(
        userEntity.id.toString(),
        userEntity.role.roles,
        mapOf("userId" to userEntity.id.toString())
      )
    ).getOrElse {
      throw CustomError.BAD_REQUEST.asException("Error generating token")
    }
    return TokenResponse(
      accessToken = token.accessToken,
      refreshToken = token.refreshToken,
      tokenType = token.tokenType,
      expiresIn = token.expiresIn,
    )
  }

  override fun refreshToken(refreshToken: String): TokenResponse {
    val refreshTokenSource = refreshTokenValidator.validate(refreshToken).getOrElse {
      throw CustomError.BAD_REQUEST.asException("Invalid refresh token")
    }

    val authentication = Mono.from(refreshTokenPersistence.getAuthentication(refreshTokenSource)).block()
      ?: throw CustomError.BAD_REQUEST.asException("Invalid refresh token")

    val token = tokenGenerator.generate(authentication).getOrElse {
      throw CustomError.BAD_REQUEST.asException("Error generating token")
    }
    return TokenResponse(
      accessToken = token.accessToken,
      refreshToken = token.refreshToken,
      tokenType = token.tokenType,
      expiresIn = token.expiresIn
    )
  }
}
