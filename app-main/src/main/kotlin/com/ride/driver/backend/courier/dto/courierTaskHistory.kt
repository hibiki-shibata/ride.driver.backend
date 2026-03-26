package com.ride.driver.backend.courier.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.DateTimeFormat

data class CourierTaskHistoryDTO(
    val id: String,
    @field:NotBlank
    val courierEarning: Double,

    @field:NotBlank
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val orderTime: String,

    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val consumerName: String,

    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val merchantName: String,
)