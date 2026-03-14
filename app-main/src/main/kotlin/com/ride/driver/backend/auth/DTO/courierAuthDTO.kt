package com.ride.driver.backend.auth.dto

import com.ride.driver.backend.courier.models.VehicleType

data class CourierSignInDTO(
    val name: String,
    val phoneNumber: String,
    val password: String,
    val vehicleType: VehicleType,
)

data class CourierLoginDTO(
    val phoneNumber: String,
    val password: String
)