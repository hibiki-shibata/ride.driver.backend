// MessageController.kt
package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam

import com.ride.driver.backend.services.CourierLoginService

@RestController
@RequestMapping("api/v1/couriers")
class courirRequestController {
    @GetMapping("/login")
    fun courierLogin(@RequestParam("name") name: String): String { 

        return CourierLoginService().courierLogin(name)
    }
    // e.g. http://localhost:4000/api/v1/couriers/login?name=This-is-the-name

}
