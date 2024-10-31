package com.alext.api.exception.handlers

import com.alext.exception.CustomError
import com.alext.logging.error
import com.alext.logging.logger
import com.alext.retrofit.ErrorResponse
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Singleton
@Produces
@Requires(classes = [Exception::class, ExceptionHandler::class])
class RuntimeExceptionHandler : ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {

  companion object {
    private val logger = RuntimeExceptionHandler::class.logger()
  }

  override fun handle(
    request: HttpRequest<*>?,
    exception: Exception
  ): HttpResponse<ErrorResponse> {
    logger.error(exception) {
      "Got an unhandled exception due to: $exception"
    }
    val error = CustomError.INTERNAL_SERVER_ERROR
    return HttpResponse
      .serverError(exception.message)
      .body(ErrorResponse(exception.message, error.code()))
      .status(error.statusCode())
  }
}
