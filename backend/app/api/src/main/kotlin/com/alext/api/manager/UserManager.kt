package com.alext.api.manager

import com.alext.client.models.request.UserCreateRequest
import com.alext.crypto.PasswordManager
import com.alext.exception.CustomError
import com.alext.postgresql.constants.UserRole
import com.alext.postgresql.entity.UserEntity
import com.alext.postgresql.repository.UserRepository
import java.util.UUID

interface UserManager {

  fun byUsername(username: String): UserEntity

  fun byUserId(userId: UUID): UserEntity

  fun addUser(request: UserCreateRequest): UserEntity

  fun addUserWithRole(request: UserCreateRequest, role: UserRole): UserEntity
}

class DefaultUserManager(
  private val userRepository: UserRepository,
  private val passwordManager: PasswordManager
) : UserManager {

  override fun byUsername(username: String): UserEntity {
    return userRepository.byUsername(username) ?: throw CustomError.NOT_FOUND.asException("User not found")
  }

  override fun byUserId(userId: UUID): UserEntity {
    return userRepository.byUserId(userId) ?: throw CustomError.NOT_FOUND.asException("User not found")
  }

  override fun addUser(request: UserCreateRequest): UserEntity {
    return addUserWithRole(request, UserRole.USER)
  }

  override fun addUserWithRole(request: UserCreateRequest, role: UserRole): UserEntity {
    val userEntity = UserEntity(
      username = request.username,
      password = passwordManager.hash(request.password),
      role = role
    )
    return userRepository.insert(userEntity)
  }
}
