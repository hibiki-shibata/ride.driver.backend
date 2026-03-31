package com.ride.driver.backend.merchant.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class MerchantItemReqDTO(
    @NotNull(message = "Item name must not be null")
    @Size(max = 30, message = "Item name must not exceed 30 characters")
    val name: String,

    @NotNull(message = "Enabled status must not be null")
    @Size(max = 100, message = "Item description must not exceed 100 characters")
    val description: String?,

    @NotNull(message = "Item price must not be null")
    val price: Double,

    @NotNull(message = "Enabled status must not be null")
    val enabled: Boolean
)