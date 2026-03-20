package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class AccountConflictException(
    message: String? = "Account Conflict Exception was called"
): HttpBaseException(
    status = HttpStatus.CONFLICT,
    message = message
)