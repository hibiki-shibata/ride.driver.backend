package com.ride.driver.backend.consumer.dto

import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ConsumerSignupDTO(
    @field:NotBlank
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String,

    @field:NotBlank
    @field:Size(max = 255, message = "Password must not exceed 255 characters")
    val consumerAddress: String,

    val consumerAddressCoordinate: Coordinate,

    @field:NotBlank
    @field:Email
    @field:Size(max = 50, message = "Email address must not exceed 50 characters")
    val emailAddress: String,
    
    @field:NotBlank
    @field:Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    val password: String
)

data class ConsumerLoginDTO(
    @field:NotBlank
    @field:Email
    val emailAddress: String,

    @field:NotBlank
    val password: String
)