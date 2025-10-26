package com.ride.driver.backend.exceptions

import com.ride.driver.backend.exceptions.HttpException

open class CustomExceptionDemo(
    message: String? = "Demo Custom Exception was called"
) : HttpException("502", message)
