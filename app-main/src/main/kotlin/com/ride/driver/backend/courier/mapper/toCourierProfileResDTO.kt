package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.logistic.model.Task

fun CourierProfile.toCourierProfileResDTO(): CourierProfileResDTO {
    return CourierProfileResDTO(
        id = this.id,
        name = this.name,
        phoneNumber = this.phoneNumber,
        vehicleType = this.vehicleType,
        cpRate = this.cpRate,
        cpStatus = this.cpStatus,
        operationArea = this.operationArea,
        cpComments = this.cpComments
    )
}