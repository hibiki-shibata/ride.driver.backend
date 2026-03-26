package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles


fun ConsumerProfile.toAccessTokenClaim(): AccessTokenClaim {
    return AccessTokenClaim(
        accountId = this.id ?: throw IllegalArgumentException("Consumer ID is null"),
        accountName = this.name,
        accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE)
    )
}