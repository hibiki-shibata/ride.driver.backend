package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.ConsumerProfileDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile

fun ConsumerProfile.toConsumerProfileDTO(): ConsumerProfileDTO {
    return ConsumerProfileDTO(
        name = this.name,
        emailAddress = this.emailAddress
    )
}