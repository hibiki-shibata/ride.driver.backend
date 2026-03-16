package com.ride.driver.backend.shared.auth.dto

import jakarta.validation.constraints.NotBlank

data class JwtTokensDTO(
    @field:NotBlank
    val accessToken: String,

    @field:NotBlank
    val refreshToken: String
)