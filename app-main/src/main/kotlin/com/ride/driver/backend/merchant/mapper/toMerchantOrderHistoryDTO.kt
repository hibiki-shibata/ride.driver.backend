package com.ride.driver.backend.merchant.mapper


import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import com.ride.driver.backend.logistic.model.Task

fun Task.toMerchantOrderHistoryDto(): MerchantOrderHistoryDTO {
    return MerchantOrderHistoryDTO(
        id = this.id?.toString() ?: "Unknown order ID",
        consumerName = this.consumerProfile?.name ?: "Unknown consumer name",
        orderStatus = this.taskStatus.toString(),
        orderTime = this.orderTime.toString()

    )
}
