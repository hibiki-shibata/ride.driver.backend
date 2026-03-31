package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class PasswordEncodingException(
    message: String? = "Password Encoding Exception was called"
): HttpBaseException(
    status = HttpStatus.UNAUTHORIZED,
    message = message
)