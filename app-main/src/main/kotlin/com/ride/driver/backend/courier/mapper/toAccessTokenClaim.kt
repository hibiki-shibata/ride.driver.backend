package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles

fun CourierProfile.toAccessTokenClaim(): AccessTokenClaim {
    return AccessTokenClaim(
        accountID = this.id ?: throw IllegalArgumentException("courier ID is null"),
        accountName = this.name,
        accountRoles = listOf(AccountRoles.BASE_COURIER_ROLE)
    )
}