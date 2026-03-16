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

data class ConsumerOrderHistoryDTO(
    @field:NotBlank
    val merchantName: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:NotBlank 
    val orderTime: String,

    @field:NotBlank
    val orderStatus: String,
)