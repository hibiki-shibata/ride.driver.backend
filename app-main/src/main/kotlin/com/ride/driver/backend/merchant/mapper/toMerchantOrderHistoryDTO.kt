package com.ride.driver.backend.merchant.mapper


import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun Task.toMerchantOrderHistoryDto(): MerchantOrderHistoryDTO {
    return MerchantOrderHistoryDTO(
        id = this.id?.toString() ?: throw AccountInvalidValuesException("Profile mapping failed, Merchant ID is null"),
        consumerName = this.consumerProfile?.name ?: "Unknown consumer name",
        orderStatus = this.taskStatus.toString(),
        orderTime = this.orderTime.toString()

    )
}
