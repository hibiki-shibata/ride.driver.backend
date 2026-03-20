package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus

open class HttpBaseException(
    val status: HttpStatus,
    override val message: String? = null,
) : RuntimeException(message)