package com.alext.exception

import io.micronaut.http.HttpStatus

/**
 * This is a detail of custom errors.
 * A custom error includes http status, message and description.
 * In general, these errors are reserved for friendly client error messages.
 */
enum class CustomError(
  private val status: HttpStatus,
  private val message: String
) {
  INTERNAL_SERVER_ERROR(
    HttpStatus.INTERNAL_SERVER_ERROR,
    "The server cannot process the request for an unknown reason"
  ),
  BAD_REQUEST(
    HttpStatus.BAD_REQUEST,
    "Bad request"
  ),
  NOT_FOUND(
    HttpStatus.NOT_FOUND,
    "Resource not found"
  ),
  UNAUTHORIZED(
    HttpStatus.UNAUTHORIZED,
    "Unauthorized"
  ),
  FORBIDDEN(
    HttpStatus.FORBIDDEN,
    "Permission denied"
  );

  /**
   * Get platform error code.
   *
   * @return The unique code specified to this error.
   */
  fun code(): String = this.name

  /**
   * Get platform error message.
   *
   * @return The detail message specified to this error.
   */
  fun message(): String = buildMessage()

  /**
   * Get the http status code of a platform error.
   *
   * @return The http status code specified to this error.
   */
  fun statusCode(): Int = this.status.code

  /**
   * Convert a platform error to a client exception with default message.
   *
   * @return The [ClientException]
   */
  fun asException(): ClientException = asException(null)

  /**
   * Convert a platform error to a client exception.
   *
   * @param customMessage The custom message for platform error.
   * @return A [ClientException]
   */
  fun asException(customMessage: String?): ClientException {
    return ClientException(
      httpStatusCode = this.status.code,
      code = this.code(),
      message = buildMessage(customMessage)
    )
  }

  private fun buildMessage(message: String? = null): String {
    return message ?: this.message
  }
}
