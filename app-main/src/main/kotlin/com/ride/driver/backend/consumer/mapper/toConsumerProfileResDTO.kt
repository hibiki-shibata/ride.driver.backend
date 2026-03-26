package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile

fun ConsumerProfile.toConsumerProfileResDTO(): ConsumerProfileResDTO {
    return ConsumerProfileResDTO(
        id = this.id?.toString() ?: "Consumer id not found",
        name = this.name,
        emailAddress = this.emailAddress,
        consumerAddress = this.consumerAddress,
        consumerAddressCoordinate = this.consumerAddressCoordinate
    )
}