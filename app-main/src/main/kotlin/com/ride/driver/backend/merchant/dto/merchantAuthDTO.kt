package com.ride.driver.backend.merchant.dto

import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class MerchantSignupDTO(
    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    val name: String,

    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    val password: String,

    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    val merchantAddress: String,

    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    val merchantComments: String?,

    @field:NotBlank
    val merchantAddressCoordinate: Coordinate
)

data class MerchantLoginDTO(
    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    @field:Size(xmax = 50, message = "Name must not exceed 100 characters")
    val password: String
)