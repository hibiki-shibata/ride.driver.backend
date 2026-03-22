package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat

data class ConsumerProfileDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Email
    val emailAddress: String,
)