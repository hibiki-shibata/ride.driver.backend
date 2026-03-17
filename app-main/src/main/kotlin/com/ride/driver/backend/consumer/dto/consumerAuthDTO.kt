package com.ride.driver.backend.consumer.dto

import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ConsumerSignInDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val consumerAddress: String,

    @field:NotBlank
    val consumerAddressCoordinate: Coordinate,

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