package com.ride.driver.backend.merchant.mapper

import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun MerchantProfile.toAccessTokenClaim(): AccessTokenClaim {
    return AccessTokenClaim(
        accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed, Merchant ID is null"),
        accountName = this.name,
        accountRoles = listOf(AccountRoles.BASE_MERCHANT_ROLE)
    )
}