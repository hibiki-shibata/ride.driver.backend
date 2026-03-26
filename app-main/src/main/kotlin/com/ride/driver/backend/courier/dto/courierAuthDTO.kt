package com.ride.driver.backend.courier.dto

import com.ride.driver.backend.courier.model.VehicleType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CourierSignupDTO(
    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val name: String,
    
    @field:NotBlank
    @field:Size(max = 15, message = "Name must not exceed 15 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format") 
    val phoneNumber: String, // review

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val password: String,

    val vehicleType: VehicleType,
)

data class CourierLoginDTO(
    @field:NotBlank
    @field:Size(max = 15, message = "Name must not exceed 15 characters")
    val phoneNumber: String,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val password: String
)