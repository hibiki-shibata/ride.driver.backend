package com.ride.driver.backend.dto

import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.OperationArea
import java.util.UUID
// import kotlinx.serialization.Serializable

// @Serializable
data class CourierProfileDTO(
    val id: UUID? = null,
    val name: String,
    val phoneNumber: String,
    val vehicleType: String,
    val rate: Double,
    val status: String,
    val operationArea: OperationArea?,
    val comments: String?
)