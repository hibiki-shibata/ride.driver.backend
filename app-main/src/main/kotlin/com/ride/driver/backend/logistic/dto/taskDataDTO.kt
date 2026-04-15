package com.ride.driver.backend.logistic.dto

data class TaskDataDTO(
    val taskId: String,
    val consumerName: String,
    val consumerEmailaddress: String,
    val merchantName: String,
    val pickupAddress: String,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val dropoffAddress: String,
    val dropoffLatitude: Double,
    val dropoffLongitude: Double,
    val itemNames: List<String>,
    val totalPrice: Double
)