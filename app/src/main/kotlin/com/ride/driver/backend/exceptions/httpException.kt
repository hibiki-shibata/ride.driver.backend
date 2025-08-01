package com.ride.driver.backend.exceptions

open class httpException(
    val status: String? = "500",
    override val message: String? = null,
) : RuntimeException(message)

