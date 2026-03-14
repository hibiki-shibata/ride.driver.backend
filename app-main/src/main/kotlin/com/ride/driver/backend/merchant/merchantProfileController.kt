package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import com.ride.driver.backend.services.AccessTokenData
import com.ride.driver.backend.models.Coordinate
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.logistics.TaskStatus
import java.util.UUID
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.TaskRepository
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantsController (
    private val taskRepository: TaskRepository
){
    @PostMapping("/merchant/me")
    fun findmerchantProfile(@RequestBody taskStatusActionDTO: TaskStatusActionDTO): ResponseEntity<String> {
        // merchant profile update location logic goes here. For now, just return a success message.
        return ResponseEntity.ok("merchant location updated successfully")
    }
}