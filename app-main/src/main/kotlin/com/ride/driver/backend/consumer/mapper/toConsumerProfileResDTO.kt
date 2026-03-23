package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile

fun ConsumerProfile.toConsumerProfileResDTO(): ConsumerProfileResDTO {
    return ConsumerProfileResDTO(
        name = this.name,
        emailAddress = this.emailAddress,
        consumerAddress = this.consumerAddress,
        consumerAddressCoordinate = this.consumerAddressCoordinate
    )
}