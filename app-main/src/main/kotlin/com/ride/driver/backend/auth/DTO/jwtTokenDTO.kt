package com.ride.driver.backend.auth.dto

data class JwtTokensDTO(
    val accessToken: String,
    val refreshToken: String
)