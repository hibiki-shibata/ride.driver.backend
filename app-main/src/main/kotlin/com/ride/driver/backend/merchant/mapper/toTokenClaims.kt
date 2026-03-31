package com.ride.driver.backend.merchant.mapper

import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.domain.JwtTokenClaims
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun MerchantProfile.toTokenClaims(): JwtTokenClaims {
    return JwtTokenClaims(
        accessTokenClaim = AccessTokenClaim(
                accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed, Merchant ID is null"),
                accountName = this.name,
                accountRoles = listOf(AccountRoles.BASE_MERCHANT_ROLE)
        ),
        refreshTokenClaim = RefreshTokenClaim(
                accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed, Merchant ID is null"),
                serviceType = ServiceType.MERCHANT,
        )
    )
}