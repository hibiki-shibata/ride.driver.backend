package com.ride.driver.backend.courier.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType


data class CourierProfileReqDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    val vehicleType: VehicleType?,

    val cpStatus: CourierStatus,

    val operationArea: OperationArea?,

    @field:NotBlank
    val cpComments: String?
)