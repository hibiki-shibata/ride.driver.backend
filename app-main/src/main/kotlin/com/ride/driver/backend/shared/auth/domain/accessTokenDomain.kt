package com.ride.driver.backend.shared.auth.domain

import java.util.UUID

enum class AccountRoles {
    BASE_ROLE,
    ADMIN_ROLE,
    DEVELOPER_ROLE
}

data class AccessTokenData(
    val accountID: UUID,
    val accountName: String,
    val accountRoles: List<AccountRoles>,
)