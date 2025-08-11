package com.ride.driver.backend.dto

import com.ride.driver.backend.models.DriverStatus
import com.ride.driver.backend.models.Location
import com.ride.driver.backend.models.VehicleType
import java.util.UUID

// import kotlinx.serialization.Serializable

// @Serializable
data class DeliveryDetailsDTO(
    val id: UUID? = null,
    val name: String,
    val phoneNumber: String,
    val vehicleType: String,
    val location: LocationDTO,
    val assignId: String?,
    val rate: Double,
    val status: String,
    val area: AreaDTO?,
    val driverComments: String?
)

