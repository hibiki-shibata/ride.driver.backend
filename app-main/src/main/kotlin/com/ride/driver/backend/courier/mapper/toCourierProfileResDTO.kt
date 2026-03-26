package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.logistic.model.Task

fun CourierProfileResDTO.toCourierProfileResDTO(): CourierProfileResDTO {
    return CourierProfileResDTO(
        id = this.id :? "courier id not found",
        name = this.name,
        phoneNumber = this.phoneNumber,
        vehicleType = this.vehicleType,
        rate = this.cpRate,
        operationArea = this.operationArea,
        comments = this.cpComments
    )
}