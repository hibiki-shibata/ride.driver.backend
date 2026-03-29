package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun ConsumerProfile.toRefreshTokenClaim(): RefreshTokenClaim {
    return RefreshTokenClaim(
        accountId = this.id ?: throw AccountInvalidValuesException("Consumer ID is null"),
        serviceType = ServiceType.CONSUMER,
    )
}