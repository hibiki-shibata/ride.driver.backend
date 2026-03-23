package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import jakarta.validation.constraints.Size

data class ConsumerOrderHistoryDTO(
    @field:NotBlank
    @field:Size(max = 100, message = "Merchant name must not exceed 100 characters")
    val merchantName: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:NotBlank 
    val orderTime: String,

    @field:NotBlank
    val orderStatus: String,
)