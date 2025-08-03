package com.ride.driver.backend.dto

import com.ride.driver.backend.models.DriverStatus
import com.ride.driver.backend.models.Location
import com.ride.driver.backend.models.VehicleType
import java.util.UUID

data class DriverDetailsDto(
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

