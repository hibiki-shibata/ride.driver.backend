package com.ride.driver.backend.courier.dto

import com.ride.driver.backend.courier.model.VehicleType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CourierSignupDTO(
    @field:NotBlank
    val name: String,
    
    @field:NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format") 
    val phoneNumber: String, // review

    @field:NotBlank
    val password: String,

    val vehicleType: VehicleType,
)

data class CourierLoginDTO(
    @field:NotBlank
    val phoneNumber: String,

    @field:NotBlank
    val password: String
)