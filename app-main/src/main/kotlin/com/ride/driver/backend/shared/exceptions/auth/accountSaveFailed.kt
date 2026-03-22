package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class AccountSaveFailedException(
    message: String? = "Account Save Failed Exception was called"
): HttpBaseException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = message
)