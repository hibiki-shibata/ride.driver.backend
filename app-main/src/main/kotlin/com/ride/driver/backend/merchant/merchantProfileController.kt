package com.ride.driver.backend.merchant.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.models.Coordinate
import java.util.UUID
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantProfileController (
    private val taskRepository: TaskRepository
){
    @GetMapping("/merchant/me")
    fun findmerchantProfile(@RequestBody string: String): ResponseEntity<String> {
        // merchant profile update location logic goes here. For now, just return a success message.
        return ResponseEntity.ok("merchant location updated successfully")
    }
}