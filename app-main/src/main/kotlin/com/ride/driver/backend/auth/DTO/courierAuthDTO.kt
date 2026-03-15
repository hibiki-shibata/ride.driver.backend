package com.ride.driver.backend.auth.dto

import com.ride.driver.backend.courier.models.VehicleType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CourierSignInDTO(
    @field:NotBlank
    val name: String,
    
    @field:NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format") 
    val phoneNumber: String, // review

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val vehicleType: VehicleType,
)

data class CourierLoginDTO(
    @field:NotBlank
    val phoneNumber: String,

    @field:NotBlank
    val password: String
)