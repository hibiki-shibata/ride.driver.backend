package com.ride.driver.backend.exceptions

import com.ride.driver.backend.exceptions.httpException

open class HibikiSpecialException(
    message: String? = "Custom exception occurred"
) : httpException("400", message)
