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
import com.ride.driver.backend.exceptions.CustomExceptionDemo

import com.ride.driver.backend.services.CourierLoginService
import com.ride.driver.backend.services.CourierDataService

import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.OperationAreaRepository

import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea

import com.ride.driver.backend.dto.CourierProfileDTO

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString


@RestController
@RequestMapping("api/v1/couriers")
class courirRequestController (   
    private val service: CourierLoginService, 
    private val repository: CourierProfileRepository,
    private val operationAreaRepository: OperationAreaRepository

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
        if(token != "123") throw CustomExceptionDemo("logout Failed!!!!!")
        return ResponseEntity.ok("Logout successfully: $token")
    }



    @PostMapping("/register")
        fun registerCourier(@RequestBody courier: CourierProfile): ResponseEntity<String> {
        val verifiedArea: OperationArea? = courier.operationArea?.let { requestArea ->
            operationAreaRepository.findByName(requestArea.name)?.firstOrNull()
                ?: operationAreaRepository.save(requestArea)
        }

        courier.copy(operationArea = verifiedArea)
        
        println("Registering courier: ${courier.name}")
        println(courier)
        repository.save(courier)
        return ResponseEntity.ok("Courier registered successfully: ${courier.name}")
    }



    @GetMapping("/findall")
    fun findCourier(): ResponseEntity<List<CourierProfileDTO>> {        
        println("Finding all couriers...")
        val couriers: List<CourierProfile> = repository.findAll()
        
        if (couriers.none()) {
            throw CustomExceptionDemo("No couriers found")
        }
     
        // val toJson = couriers.joinToString(separator = ", ") { 
        //     """{ "id": "${it.id}", "name": "${it.name}", "phoneNumber": "${it.phoneNumber}", "vehicleType": "${it.vehicleType}", "location": { "latitude": "${it.location.latitude}", "longitude": "${it.location.longitude}" }, "assignId": "${it.assignId}", "rate": "${it.rate}", "status": "${it.status}", "area": { "name": "${it.area?.name}" }, "driverComments": "${it.driverComments}" }"""
        // }
        // val responseJson = """{ "couriers": [$toJson] }"""

        val result = couriers.map { courier ->
            CourierProfileDTO(
                id = courier.id,
                name = courier.name,
                phoneNumber = courier.phoneNumber,
                vehicleType = courier.vehicleType.toString(),
                rate = courier.rate,
                status = courier.status.toString(),
                operationArea = courier.operationArea?.let { OperationArea(name = it.name) },
                comments = courier.comments
            )
        }
    
        return ResponseEntity.ok(result)
    }


    // e.g. http://localhost:4000/api/v1/couriers/login?name=This-is-the-name

}
