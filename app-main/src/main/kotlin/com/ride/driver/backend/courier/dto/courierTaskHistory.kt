package com.ride.driver.backend.courier.dto


data class CourierTaskHistoryDTO(
    val taskId: String,
    val courierEarning: Double,
    val orderTime: String,
    val consumerName: String,
    val merchantName: String,
)