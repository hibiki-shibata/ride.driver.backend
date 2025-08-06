package com.ride.driver.backend.services

import com.ride.driver.backend.models.Location
import org.springframework.stereotype.Service

@Service
class CourierLoginService {

    fun courierLogin(name: String): String {

        return Location(
            latitude = 0.0,
            longitude = 1.1
        ).toString() + "\nHello, $name! \nWelcome to the Courier Service!\nYour login was successful."
    
    }
}