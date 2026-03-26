package com.ride.driver.backend.merchant.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import com.ride.driver.backend.shared.model.Coordinate

data class MerchantProfileReqDTO(
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

    val merchantAddressCoordinate: Coordinate
)

data class MerchantOpenStatusUpdateDTO(
    val isOpen: Boolean
)