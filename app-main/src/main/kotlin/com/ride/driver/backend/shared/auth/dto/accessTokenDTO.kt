package com.ride.driver.backend.shared.auth.dto

import jakarta.validation.constraints.NotBlank

data class AccessTokenDTO(
    val accessToken: String
)