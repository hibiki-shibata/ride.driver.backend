package com.ride.driver.backend.courier.dto

import java.util.UUID

data class CourierTaskHistoryDTO(
    val id: String,
    val courierEarning: Double,
    val orderTime: String,
    val consumerName: String,
    val merchantName: String,
)