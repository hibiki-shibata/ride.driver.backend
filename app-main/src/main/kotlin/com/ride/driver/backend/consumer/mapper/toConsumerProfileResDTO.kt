package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun ConsumerProfile.toConsumerProfileResDTO(): ConsumerProfileResDTO {
    return ConsumerProfileResDTO(
        id = this.id?.toString() ?: throw AccountInvalidValuesException("Consumer ID is null"),
        name = this.name,
        emailAddress = this.emailAddress,
        consumerAddress = this.consumerAddress,
        consumerAddressCoordinate = this.consumerAddressCoordinate
    )
}