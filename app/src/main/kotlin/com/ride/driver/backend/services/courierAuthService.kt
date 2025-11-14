package com.ride.driver.backend.services

import org.springframework.stereotype.Service

@Service
class CourierAuthService {
    fun courierLogin(name: String): String {
        println("Courier $name has logged in.")
        return "Hello, $name! \nWelcome to the Courier Service!\nYour login was successful."
        // return Location(
        //     latitude = 0.0,
        //     longitude = 1.1
        // ).toString() + "\nHello, $name! \nWelcome to the Courier Service!\nYour login was successful."
    }

    fun registerNewCourierProfileInDB(username: String, password: String): String {
        // save courier profile to the database (mocked here)
        return "Courier registered successfully."
    }
}