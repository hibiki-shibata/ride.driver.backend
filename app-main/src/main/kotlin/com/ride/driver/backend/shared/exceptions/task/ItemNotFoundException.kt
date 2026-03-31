package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class ItemNotFoundException(
    message: String? = "Item Not Found Exception was called"
): HttpBaseException(
    status = HttpStatus.NOT_FOUND,
    message = message
)