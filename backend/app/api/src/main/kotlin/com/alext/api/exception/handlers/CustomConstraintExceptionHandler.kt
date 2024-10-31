package com.alext.api.exception.handlers

import com.alext.exception.CustomError
import com.alext.logging.logger
import com.alext.logging.warn
import com.alext.retrofit.ErrorResponse
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import jakarta.inject.Singleton
import jakarta.validation.ConstraintViolationException

@Produces
@Singleton
@Replaces(ConstraintExceptionHandler::class)
@Requires(classes = [ConstraintViolationException::class, ExceptionHandler::class])
class CustomConstraintExceptionHandler : ConstraintExceptionHandler(null) {

  companion object {
    private val logger = ConstraintExceptionHandler::class.logger()
  }

  override fun handle(
    request: HttpRequest<*>?,
    exception: ConstraintViolationException
  ): HttpResponse<*> {
    logger.warn(exception) {
      "Caught a constraint exception with: ${exception.message}"
    }
    val error = CustomError.BAD_REQUEST
    return HttpResponse
      .serverError(exception.message)
      .body(ErrorResponse(exception.message, error.code()))
      .status(error.statusCode())
  }
}
