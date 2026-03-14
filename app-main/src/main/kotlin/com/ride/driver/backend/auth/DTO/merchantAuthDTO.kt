package com.ride.driver.backend.auth.dto

import com.ride.driver.backend.shared.models.Coordinate

data class MerchantSignInDTO(
    val name: String,
    val phoneNumber: String,
    val password: String,
    val merchantAddress: String,
    val merchantComments: String?,
    val merchantAddressCoordinate: Coordinate
)

data class MerchantLoginDTO(
    val phoneNumber: String,
    val password: String
)