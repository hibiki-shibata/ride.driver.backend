package com.ride.driver.backend.courier.dto

import jakarta.validation.constraints.NotBlank

data class CourierStatusUpdateDTO(
    @field:NotBlank
    val isOnline: Boolean,
)