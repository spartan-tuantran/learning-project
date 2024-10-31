package com.alext.api.auth

import com.alext.api.runtime.config.AppConfiguration
import com.alext.exception.CustomError
import com.alext.postgresql.entity.RefreshTokenEntity
import com.alext.postgresql.repository.RefreshTokenRepository
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent
import io.micronaut.security.token.refresh.RefreshTokenPersistence
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

@Singleton
class CustomRefreshTokenPersistence(
  private val appConfiguration: AppConfiguration,
  private val refreshTokenRepository: RefreshTokenRepository,
) : RefreshTokenPersistence {

  override fun persistToken(event: RefreshTokenGeneratedEvent) {
    refreshTokenRepository.hardDeleteByUserId(event.authentication.userId)
    refreshTokenRepository.insert(
      RefreshTokenEntity(
        token = event.refreshToken,
        userId = event.authentication.userId,
        expiresIn = appConfiguration.refreshTokenExpiration
      )
    )
  }

  override fun getAuthentication(refreshToken: String): Publisher<Authentication> {
    val tokenEntity = refreshTokenRepository.byToken(refreshToken)
      ?: throw CustomError.NOT_FOUND.asException("Token not found")

    if (tokenEntity.isExpired()) {
      refreshTokenRepository.hardDeleteById(tokenEntity.id)
      throw CustomError.BAD_REQUEST.asException("Token is expired")
    }

    return Mono.justOrEmpty(tokenEntity)
      .map {
        Authentication.build(
          tokenEntity.userId.toString(),
          mapOf("userId" to tokenEntity.userId.toString())
        )
      }
  }
}
