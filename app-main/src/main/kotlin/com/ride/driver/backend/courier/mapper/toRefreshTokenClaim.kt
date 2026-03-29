package com.ride.driver.backend.courier.mapper

import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun CourierProfile.toRefreshTokenClaim(): RefreshTokenClaim {
    return RefreshTokenClaim(
        accountId = this.id ?: throw AccountInvalidValuesException("Profile mapping failed due to cp id is null"),
        serviceType = ServiceType.COURIER,
    )
}