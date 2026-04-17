package com.ride.driver.backend.logistic.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class CreateTaskDTO(
    @field:NotBlank
    val merchantId: UUID,

    @field:NotEmpty
    val cartItems: List<CartItem>
)


data class CartItem(
    @field:NotBlank
    val itemId: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val price: Double,

    @field:NotBlank
    val quantity: Int
)