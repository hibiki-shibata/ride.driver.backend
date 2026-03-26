package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.Size
import java.util.UUID

data class ConsumerProfileResDTO(
    @field:NotBlank
    val id: UUID,


    @field:NotBlank
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String,

    @field:NotBlank
    @field:Email
    @field:Size(max = 50, message = "Email address must not exceed 50 characters")
    val emailAddress: String,

    @field:NotBlank
    @field:Size(max = 255, message = "Consumer address must not exceed 255 characters")
    val consumerAddress: String,

    // @field:NotBlank
    val consumerAddressCoordinate: Coordinate,
)