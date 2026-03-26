package com.ride.driver.backend.courier.dto

import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType

data class CourierProfileResDTO(
    @field:NotBlank
    val id: UUID?,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val name: String,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    val vehicleType: VehicleType?,

    @field:NotBlank
    val cpRate: Double?,

    val cpStatus: CourierStatus,

    val operationArea: OperationArea?,

    @field:NotBlank
    @field:Size(max = 50, message = "Name must not exceed 100 characters")
    val cpComments: String?
)
