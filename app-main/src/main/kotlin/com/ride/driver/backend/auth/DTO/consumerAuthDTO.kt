package com.ride.driver.backend.auth.dto

import com.ride.driver.backend.shared.models.Coordinate

data class ConsumerSignInDTO(
    val name: String,
    val homeAddress: String,
    val homeAddressCoordinate: Coordinate,
    val emailAddress: String,
    val password: String
)

data class ConsumerLoginDTO(
    val emailAddress: String,
    val password: String
)