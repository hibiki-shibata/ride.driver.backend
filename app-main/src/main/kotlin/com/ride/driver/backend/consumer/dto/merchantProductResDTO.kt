package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import com.ride.driver.backend.shared.model.Coordinate
import jakarta.validation.constraints.Size

class MerchantProductResDTO(
    @field:NotBlank
    val id: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val productDescription: String?,

    @field:NotBlank
    val price: Double,
    
    @field:NotBlank
    val enabled: Boolean
)