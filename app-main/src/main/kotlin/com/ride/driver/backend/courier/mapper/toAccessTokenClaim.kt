package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun CourierProfile.toAccessTokenClaim(): AccessTokenClaim {
    return AccessTokenClaim(
        accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed due to cp id is null"),
        accountName = this.name,
        accountRoles = listOf(AccountRoles.BASE_COURIER_ROLE)
    )
}