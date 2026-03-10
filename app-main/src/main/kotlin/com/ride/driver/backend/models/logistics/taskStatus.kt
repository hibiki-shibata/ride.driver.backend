package com.ride.driver.backend.models.logistics

enum class TaskStatus {
    CREATED,
    IN_PICKUP,
    IN_DROPOFF,
    DELIVERED,
    CANCELLED
}