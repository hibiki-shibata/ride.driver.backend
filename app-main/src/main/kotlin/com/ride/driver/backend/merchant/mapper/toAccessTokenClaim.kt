package com.ride.driver.backend.merchant.mapper

import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles


fun MerchantProfile.toAccessTokenClaim(): AccessTokenClaim {
    return AccessTokenClaim(
        accountId = this.id ?: throw IllegalArgumentException("Merchant ID is null"),
        accountName = this.name,
        accountRoles = listOf(AccountRoles.BASE_MERCHANT_ROLE)
    )
}