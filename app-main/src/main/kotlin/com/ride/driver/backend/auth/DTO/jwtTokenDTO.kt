package com.ride.driver.backend.auth.dto

import jakarta.validation.constraints.NotBlank

data class JwtTokensDTO(
    @field:NotBlank
    val accessToken: String,

    @field:NotBlank
    val refreshToken: String
)