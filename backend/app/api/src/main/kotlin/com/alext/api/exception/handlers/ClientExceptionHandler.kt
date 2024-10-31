package com.alext.api.exception.handlers

import com.alext.exception.ClientException
import com.alext.logging.logger
import com.alext.logging.warn
import com.alext.retrofit.ErrorResponse
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Singleton
@Produces
@Requires(classes = [ClientException::class, ExceptionHandler::class])
class ClientExceptionHandler : ExceptionHandler<ClientException, HttpResponse<ErrorResponse>> {

  companion object {
    private val logger = ClientExceptionHandler::class.logger()
  }

  override fun handle(
    request: HttpRequest<*>?,
    exception: ClientException
  ): HttpResponse<ErrorResponse> {
    logger.warn(exception) {
      "Caught a client exception with error: ${exception.message}"
    }
    return HttpResponse
      .serverError(exception.message)
      .body(ErrorResponse(exception.message, exception.code))
      .status(exception.httpStatusCode)
  }
}
