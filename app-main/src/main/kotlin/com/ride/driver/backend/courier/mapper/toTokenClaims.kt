package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.TokenClaims
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException
import com.ride.driver.backend.courier.model.CourierProfile

fun CourierProfile.toTokenClaims(): TokenClaims {
    return TokenClaims(
        accessTokenClaim = AccessTokenClaim(
            accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed due to cp id is null"),
            accountName = this.name,
            accountRoles = listOf(AccountRoles.BASE_COURIER_ROLE)
        ),
        refreshTokenClaim = RefreshTokenClaim(
            accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed due to cp id is null"),
            serviceType = ServiceType.COURIER,
        )
    )
}