package com.ride.driver.backend.exceptions

open class HttpException(
    val status: String? = "500",
    override val message: String? = null,
) : RuntimeException(message)

