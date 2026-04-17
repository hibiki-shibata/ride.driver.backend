package com.ride.driver.backend.consumer.dto

import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import jakarta.validation.constraints.Size

data class ConsumerOrderHistoryDTO(
    @field:NotBlank
    val taskId: String,

    @field:NotBlank
    val merchantName: String,

    @field:NotBlank
    val taskStatus: String,

    @field:NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val orderTime: String,

    @field:NotBlank
    val pickupAddress: String,

    @field:NotBlank
    val pickupLatitude: Double,

    @field:NotBlank
    val pickupLongitude: Double,

    @field:NotBlank
    val dropoffAddress: String,

    @field:NotBlank
    val dropoffLatitude: Double,

    @field:NotBlank
    val dropoffLongitude: Double,

    @field:NotBlank
    val itemNames: List<String>,

    @field:NotBlank
    val totalPrice: Double
)