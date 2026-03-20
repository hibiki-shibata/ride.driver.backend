package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class InvalidJwtTokenException(
    message: String? = "Invalid JWT Token Exception was called"
): HttpBaseException(
    status = HttpStatus.UNAUTHORIZED,
    message = message
)