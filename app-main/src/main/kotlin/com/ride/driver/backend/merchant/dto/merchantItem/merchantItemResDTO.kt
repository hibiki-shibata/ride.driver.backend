package com.ride.driver.backend.merchant.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MerchantItemResDTO(
    @NotNull(message = "Item ID must not be null")
    val id: String,

    @NotNull(message = "Item name must not be null")
    val name: String,

    @NotNull(message = "Item price must not be null")

    @NotNull(message = "Enabled status must not be null")
    val description: String?,

    @NotNull(message = "Item price must not be null")
    val price: Double,

    @NotNull(message = "Enabled status must not be null")
    val enabled: Boolean
)