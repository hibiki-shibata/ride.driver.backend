package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO
import com.ride.driver.backend.logistic.model.Task

fun Task.toConsumerOrderHistoryDto(): ConsumerOrderHistoryDTO {
    return ConsumerOrderHistoryDTO(
        merchantName = this.merchantProfile?.name ?: "Unknown Merchant name",
        orderTime = this.orderTime.toString(),
        orderStatus = this.taskStatus.toString()
    )
}