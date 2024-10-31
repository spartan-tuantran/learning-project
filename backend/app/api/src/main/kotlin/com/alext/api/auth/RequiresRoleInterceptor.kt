package com.alext.api.auth

import com.alext.exception.CustomError
import com.alext.postgresql.constants.UserRole
import io.micronaut.aop.Around
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.security.utils.SecurityService
import jakarta.inject.Singleton
import kotlin.jvm.optionals.getOrElse

@MustBeDocumented
@Around
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class RequiredRoles(val roles: Array<UserRole>)

@Singleton
@RequiredRoles([])
class RequiresRoleInterceptor(
  private val securityService: SecurityService
) : MethodInterceptor<Any, Any> {

  override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
    val authentication = securityService.authentication.getOrElse {
      throw CustomError.UNAUTHORIZED.asException("Not authorized")
    }

    val hasRoles = authentication.userRoles
    val requiredRoles = context.annotationMetadata
      .getValue(RequiredRoles::class.java, "roles", Array<UserRole>::class.java)
      .orElse(emptyArray())
      .toList()

    if (!hasRoles.containsAll(requiredRoles)) {
      throw CustomError.FORBIDDEN.asException("You have not enough power to do this action")
    }

    return context.proceed()
  }
}
