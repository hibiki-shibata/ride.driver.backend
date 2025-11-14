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
import com.ride.driver.backend.services.CourierAuthService
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.OperationAreaRepository
import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea
import com.ride.driver.backend.dto.CourierProfileDTO

@RestController
@RequestMapping("api/v1/couriers")
class BusinessLogicController (   
    private val courierAuthService: CourierAuthService, 
    private val repository: CourierProfileRepository,
    private val operationAreaRepository: OperationAreaRepository

){
    @GetMapping("/login")
    fun courierLogin(@RequestParam("name") name: String): String { 
        return courierAuthService.courierLogin(name)
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
        val result = couriers.map { courier ->
            CourierProfileDTO(
                id = courier.id,
                name = courier.name,
                phoneNumber = courier.phoneNumber,
                vehicleType = courier.vehicleType.toString(),
                rate = courier.rate ?: 0.0,
                status = courier.status.toString(),
                operationArea = courier.operationArea?.let { OperationArea(name = it.name) },
                comments = courier.comments
            )
        }
        return ResponseEntity.ok(result)
    }
}