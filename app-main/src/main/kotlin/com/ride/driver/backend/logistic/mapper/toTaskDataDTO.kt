package com.ride.driver.backend.logistic.mapper

import com.ride.driver.backend.logistic.dto.TaskDataDTO
import com.ride.driver.backend.logistic.model.Task

fun Task.toTaskDataDTO(): TaskDataDTO {
    return TaskDataDTO(
        taskId = id.toString(),
        consumerName = consumerProfile?.name ?: "Unknown Consumer",
        consumerEmailaddress = consumerProfile?.emailAddress ?: "Unknown Phone Number",
        merchantName = merchantProfile?.name ?: "Unknown Merchant",
        pickupAddress = merchantProfile?.merchantAddress ?: "Unknown Pickup Address",
        pickupLatitude = merchantProfile?.merchantAddressCoordinate?.latitude ?: 0.0,
        pickupLongitude = merchantProfile?.merchantAddressCoordinate?.longitude ?: 0.0,
        dropoffAddress = consumerProfile?.consumerAddress ?: "Unknown Dropoff Address",
        dropoffLatitude = consumerProfile?.consumerAddressCoordinate?.latitude ?: 0.0,
        dropoffLongitude = consumerProfile?.consumerAddressCoordinate?.longitude ?: 0.0,
        itemNames = orderedItems.map { it.name },
        totalPrice = totalPrice
    )
}