package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat


data class ConsumerOrderHistoryDTO(
    @field:NotBlank
    val merchantName: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:NotBlank 
    val orderTime: String,

    @field:NotBlank
    val orderStatus: String,
)