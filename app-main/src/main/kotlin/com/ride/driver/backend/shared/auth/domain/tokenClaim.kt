package com.ride.driver.backend.shared.auth.domain

data class JwtTokenClaims(
    val accessTokenClaim: AccessTokenClaim,
    val refreshTokenClaim: RefreshTokenClaim
)