package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun Task.toCourierTaskHistoryDto(): CourierTaskHistoryDTO {
    return CourierTaskHistoryDTO(
        id = this.id.toString() ?: throw AccountInvalidValuesException("Profile mapping failed due to cp id is null"),
        courierEarning = this.courierEarning, 
        orderTime = this.orderTime.toString(),
        consumerName = this.consumerProfile?.name ?: "Unknown consumer",
        merchantName = this.merchantProfile?.name ?: "Unknown merchant"
    )
}