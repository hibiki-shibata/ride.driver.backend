package com.ride.driver.backend.shared.auth.domain

data class TokenClaims(
    val accessTokenClaim: AccessTokenClaim,
    val refreshTokenClaim: RefreshTokenClaim
)