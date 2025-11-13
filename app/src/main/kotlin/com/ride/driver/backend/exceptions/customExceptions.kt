package com.ride.driver.backend.exceptions

open class HttpException(
    val status: String? = "500",
    override val message: String? = null,
) : RuntimeException(message)

open class CustomExceptionDemo(
    message: String? = "Demo Custom Exception was called"
) : HttpException("400", message)

open class AuthenticationException(
    message: String? = "Authentication Exception was called"
) : Exception(message)