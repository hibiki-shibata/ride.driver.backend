package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO
import com.ride.driver.backend.logistic.model.Task

fun Task.toConsumerOrderHistoryDto(): ConsumerOrderHistoryDTO {
    return ConsumerOrderHistoryDTO(
        taskId = this.id.toString(),
        merchantName = this.merchantProfile.name,
        taskStatus = this.taskStatus.name,
        orderTime = this.orderTime.toString(),
        pickupAddress = this.consumerProfile.consumerAddress,
        pickupLatitude = this.consumerProfile.consumerAddressCoordinate.latitude,
        pickupLongitude = this.consumerProfile.consumerAddressCoordinate.longitude,
        dropoffAddress = this.merchantProfile.merchantAddress,
        dropoffLatitude = this.merchantProfile.merchantAddressCoordinate.latitude,
        dropoffLongitude = this.merchantProfile.merchantAddressCoordinate.longitude,
        itemNames = this.orderedItems.map { it.name },
        totalPrice = this.orderedItems.sumOf { it.price }
    )
}