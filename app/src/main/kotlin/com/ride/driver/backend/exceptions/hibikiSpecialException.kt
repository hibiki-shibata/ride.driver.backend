package com.ride.driver.backend.exceptions

import com.ride.driver.backend.exceptions.httpException

open class HibikiSpecialException(
    message: String? = "hibiki Special Exception occurred"
) : httpException("502", message)
