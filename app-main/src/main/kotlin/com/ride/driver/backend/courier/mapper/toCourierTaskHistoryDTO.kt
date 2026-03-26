package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.logistic.model.Task

fun Task.toCourierTaskHistoryDto(): CourierTaskHistoryDTO {
    return CourierTaskHistoryDTO(
        id = this.id.toString() ?: "Task ID not found",
        courierEarning = this.courierEarning, 
        orderTime = this.orderTime.toString(),
        consumerName = this.consumerProfile?.name ?: "Unknown consumer",
    )
}