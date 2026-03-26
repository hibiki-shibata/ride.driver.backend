package com.ride.driver.backend.merchant.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import com.ride.driver.backend.shared.model.Coordinate

data class MerchantProfileResDTO(
    @field:NotBlank
    val id: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    val merchantAddress: String,

    val merchantComments: String? = null,

    @field:NotBlank
    val merchantStatus: String,

    // @field:NotBlank
    val merchantAddressCoordinate: Coordinate
)

data class MerchantOpenStatusUpdateDTO(
    val isOpen: Boolean
)