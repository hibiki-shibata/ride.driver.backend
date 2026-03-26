package com.ride.driver.backend.merchant.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank

data class MerchantOrderHistoryDTO(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val consumerName: String,
    @field:NotBlank
    val orderStatus: String,
    @field:NotBlank
    val orderTime: String
)

