// MessageController.kt
// package com.ride.driver.backend.controller
package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader


import org.springframework.http.ResponseEntity
import com.ride.driver.backend.exceptions.HibikiSpecialException


import com.ride.driver.backend.services.CourierLoginService
import com.ride.driver.backend.services.CourierDataService

@RestController
@RequestMapping("api/v1/couriers")
class courirRequestController (
    private val service: CourierLoginService, 
    private val dataService: CourierDataService   
){
    
    @GetMapping("/login")
    fun courierLogin(@RequestParam("name") name: String): String { 
        return service.courierLogin(name)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: Map<String, String>): ResponseEntity<String> {
        val refreshToken = request["refreshToken"] ?: return ResponseEntity.badRequest().body("Refresh Token is required")                                   
        return ResponseEntity.ok("Token refreshed successfully for user: $refreshToken")
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader ("Authorization") token: String): ResponseEntity<String> {
        if(token != "123") throw HibikiSpecialException("logout Failed!!!!!")
        return ResponseEntity.ok("Logout successfully: $token")
    }
            
    @PostMapping("/register")
    fun registerCourier(@RequestBody courierDetails: Map<String, String>): ResponseEntity<String> {
        println(courierDetails)
        dataService.saveCoureirData()
        return ResponseEntity.ok("Courier registered successfully")            

    }
    
    // e.g. http://localhost:4000/api/v1/couriers/login?name=This-is-the-name

}
