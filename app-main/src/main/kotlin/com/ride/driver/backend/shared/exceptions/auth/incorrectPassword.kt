package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class IncorrectPasswordException(
    message: String? = "Incorrect Password Exception was called"
): HttpBaseException(
    status = HttpStatus.UNAUTHORIZED,
    message = message
)