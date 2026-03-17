package com.ride.driver.backend.logistic.dto

import jakarta.validation.constraints.NotBlank

data class TaskStatusActionDTO(
    @field:NotBlank
    val taskId: String
)