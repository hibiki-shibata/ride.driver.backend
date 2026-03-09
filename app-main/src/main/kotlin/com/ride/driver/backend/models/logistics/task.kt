package com.ride.driver.backend.models.logistics

data class Task(
    val id: Long,
    val pickupLocation: TaskLocation,
    val dropoffLocation: TaskLocation,
    val description: String,
    val consumerName: String,
    val venueName: String,
    val status: TaskStatus
)