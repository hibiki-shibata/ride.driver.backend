package com.ride.driver.backend.shared.auth.domain

import java.util.UUID

enum class ServiceType {
    CONSUMER,
    COURIER,
    MERCHANT
}

data class RefreshTokenClaim (
    val accountId: UUID,
    val serviceType: ServiceType
)