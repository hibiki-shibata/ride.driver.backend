package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.shared.auth.domain.TokenClaims
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun ConsumerProfile.toTokenClaims(): TokenClaims {
    return TokenClaims(
        accessTokenClaim = AccessTokenClaim(
            accountId = this.id ?: throw AccountInvalidValuesException("Consumer ID is null"),
            accountName = this.name,
            accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE),
            serviceType = ServiceType.CONSUMER
        ),
        refreshTokenClaim = RefreshTokenClaim(
            accountId = this.id ?: throw AccountInvalidValuesException("Consumer ID is null"),
            serviceType = ServiceType.CONSUMER,
        )
    )
}