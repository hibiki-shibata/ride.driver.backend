package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class AccountInvalidInputException(
    message: String? = "Account Invalid Input Exception was called"
): HttpBaseException(
    status = HttpStatus.BAD_REQUEST,
    message = message
)