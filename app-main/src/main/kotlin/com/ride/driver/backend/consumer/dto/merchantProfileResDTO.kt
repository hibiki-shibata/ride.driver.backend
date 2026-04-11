package com.ride.driver.backend.consumer.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.Size

data class MerchantProfileResDTO(
    @field:NotBlank
    val id: String,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val name: String,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val merchantAddress: String,

    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val merchantComments: String? = null,

    // @field:NotBlank
    val merchantAddressCoordinate: Coordinate
)

data class MerchantOpenStatusUpdateDTO(
    val isOpen: Boolean
)