package com.ride.driver.backend.merchant.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat

data class MerchantOrderHistoryDTO(
    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    val id: String,

    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    @field:NotBlank
    val consumerName: String,

    @field:NotBlank
    val orderStatus: String,

    @field:NotBlank
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val orderTime: String
)

