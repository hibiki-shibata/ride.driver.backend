package com.ride.driver.backend.merchant.dto

import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class MerchantSignupDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val merchantAddress: String,

    @field:NotBlank
    val merchantComments: String?,

    @field:NotBlank
    val merchantAddressCoordinate: Coordinate
)

data class MerchantLoginDTO(
    @field:NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    val password: String
)