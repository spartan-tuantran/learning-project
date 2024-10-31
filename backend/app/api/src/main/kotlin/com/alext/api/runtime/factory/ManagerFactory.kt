package com.alext.api.runtime.factory

import com.alext.api.manager.AuthManager
import com.alext.api.manager.DefaultAuthManager
import com.alext.api.manager.DefaultTokenManager
import com.alext.api.manager.DefaultUserManager
import com.alext.api.manager.TokenManager
import com.alext.api.manager.UserManager
import com.alext.crypto.DefaultPasswordManager
import com.alext.crypto.PasswordManager
import com.alext.postgresql.repository.UserRepository
import io.micronaut.context.annotation.Factory
import io.micronaut.security.token.generator.AccessRefreshTokenGenerator
import io.micronaut.security.token.refresh.RefreshTokenPersistence
import io.micronaut.security.token.validator.RefreshTokenValidator
import jakarta.inject.Singleton

@Factory
class ManagerFactory {

  @Singleton
  fun providePasswordManager(): PasswordManager {
    return DefaultPasswordManager(round = 10)
  }

  @Singleton
  fun provideUserManager(
    userRepository: UserRepository,
    passwordManager: PasswordManager
  ): UserManager {
    return DefaultUserManager(
      userRepository = userRepository,
      passwordManager = passwordManager
    )
  }

  @Singleton
  fun provideTokenManager(
    accessRefreshTokenGenerator: AccessRefreshTokenGenerator,
    refreshTokenValidator: RefreshTokenValidator,
    refreshTokenPersistence: RefreshTokenPersistence
  ): TokenManager {
    return DefaultTokenManager(
      tokenGenerator = accessRefreshTokenGenerator,
      refreshTokenValidator = refreshTokenValidator,
      refreshTokenPersistence = refreshTokenPersistence
    )
  }

  @Singleton
  fun provideAuthManager(
    userRepository: UserRepository,
    tokenManager: TokenManager,
    passwordManager: PasswordManager
  ): AuthManager {
    return DefaultAuthManager(
      userRepository = userRepository,
      tokenManager = tokenManager,
      passwordManager = passwordManager
    )
  }
}
