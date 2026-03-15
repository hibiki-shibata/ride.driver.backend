package com.ride.driver.backend.auth.dto

import com.ride.driver.backend.shared.models.Coordinate
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ConsumerSignInDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val homeAddress: String,

    @field:NotBlank
    val homeAddressCoordinate: Coordinate,

    @field:NotBlank
    @field:Email
    val emailAddress: String,
    
    @field:NotBlank
    val password: String
)

data class ConsumerLoginDTO(
    @field:NotBlank
    @field:Email
    val emailAddress: String,

    @field:NotBlank
    val password: String
)