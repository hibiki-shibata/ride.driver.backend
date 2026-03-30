package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class TaskNotFoundException(
    message: String? = "Task Not Found Exception was called"
): HttpBaseException(
    status = HttpStatus.NOT_FOUND,
    message = message
)