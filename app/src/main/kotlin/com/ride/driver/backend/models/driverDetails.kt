package com.ride.driver.backend.models

import java.util.UUID

import com.ride.driver.backend.models.*

data class Driver(
    val id: UUID,
    val phoneNumber: String,
    val name: String,
    val vehicleType: VehicleType,
    val location: Location,
    val assignID: String,
    val rate: Double,
    val status: DriverStatus = DriverStatus.AVAILABLE,
    val area: String,
    val driverComments: String = "",
)







