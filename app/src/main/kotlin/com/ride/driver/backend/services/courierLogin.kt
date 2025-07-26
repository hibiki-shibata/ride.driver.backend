package com.ride.driver.backend.services

import com.ride.driver.backend.models.Location

class CourierLoginService {

    fun courierLogin(name: String): String {
        // return "Hello, $name! \nWelcome to the Courier Service!\nYour login was successful."

        return Location(
            latitude = 0.0,
            longitude = 1.1
        ).toString() + "\nHello, $name! \nWelcome to the Courier Service!\nYour login was successful."
    
    }
}