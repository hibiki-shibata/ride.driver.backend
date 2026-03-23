package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import com.ride.driver.backend.shared.model.Coordinate

data class ConsumerProfileReqDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Email
    val emailAddress: String,

    @field:NotBlank
    val consumerAddress: String,

    // @field:NotBlank
    val consumerAddressCoordinate: Coordinate,

    @field:NotBlank
    val password: String,
)