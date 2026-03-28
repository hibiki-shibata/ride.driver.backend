package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class AccountInvalidValuesException(
    message: String? = "Account Invalid Value Exception was called"
): HttpBaseException(
    status = HttpStatus.BAD_REQUEST,
    message = message
)