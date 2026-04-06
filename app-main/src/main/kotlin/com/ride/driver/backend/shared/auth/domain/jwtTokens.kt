package com.ride.driver.backend.shared.auth.domain

import jakarta.validation.constraints.NotBlank

data class JwtTokens(
    @field:NotBlank
    val accessToken: String,

    @field:NotBlank
    val refreshToken: String
)