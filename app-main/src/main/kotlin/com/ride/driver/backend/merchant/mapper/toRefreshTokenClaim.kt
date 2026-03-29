package com.ride.driver.backend.merchant.mapper

import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun MerchantProfile.toRefreshTokenClaim(): RefreshTokenClaim {
    return RefreshTokenClaim(
        accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed, Merchant ID is null"),
        serviceType = ServiceType.MERCHANT,
    )
}