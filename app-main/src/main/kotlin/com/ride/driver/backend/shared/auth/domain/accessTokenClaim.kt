package com.ride.driver.backend.shared.auth.domain

import java.util.UUID

enum class AccountRoles {
    BASE_CONSUMER_ROLE,
    BASE_COURIER_ROLE,
    BASE_MERCHANT_ROLE,
    ADMIN_ROLE,
    DEVELOPER_ROLE
}

data class AccessTokenClaim(
    val accountId: UUID,
    val accountName: String,
    val accountRoles: List<AccountRoles>,
)