package com.ride.driver.backend.exceptions

import com.ride.driver.backend.exceptions.HttpException

open class CustomExceptionDemo(
    message: String? = "hibiki Special Exception occurred"
) : HttpException("502", message)
