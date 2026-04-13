package com.ride.driver.backend.logistic.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class CreateTaskDTO(
    @field:NotBlank
    val merchantID: UUID,

    @field:NotEmpty
    val selectedItems: List<SelectedItemDTO>
)


data class SelectedItemDTO(
    val itemID: String,
    val quantity: Int
)