package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.courier.dto.CourierOrderHistoryDTO
import com.ride.driver.backend.logistic.model.Task

fun Task.toCourierOrderHistoryDto(): CourierOrderHistoryDTO {
    return CourierOrderHistoryDTO(
        merchantName = this.merchantProfile?.name ?: "Unknown Merchant name",
        orderTime = this.orderTime.toString(),
        orderStatus = this.taskStatus.toString()
    )
}