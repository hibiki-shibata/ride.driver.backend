package com.ride.driver.backend.shared.auth.domain

import java.util.UUID

data class RefreshTokenClaim (
    val accountId: UUID,
    val serviceType: ServiceType
)