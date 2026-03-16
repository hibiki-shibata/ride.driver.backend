package com.ride.driver.backend.merchant.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.model.Coordinate
import java.util.UUID
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantProfileController (
    private val taskRepository: TaskRepository
){
    @GetMapping("/merchant/me")
    fun findmerchantProfile(
        @RequestBody string: String,
        @AuthenticationPrincipal merchantDetails: AccessTokenData
    ): ResponseEntity<String> {
        // merchant profile update location logic goes here. For now, just return a success message.
        return ResponseEntity.ok("merchant location updated successfully")
    }
}