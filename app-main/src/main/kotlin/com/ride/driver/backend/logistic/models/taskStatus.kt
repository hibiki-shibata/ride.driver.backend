package com.ride.driver.backend.logistic.models

enum class TaskStatus {
    CREATED,
    READY_FOR_ASSIGNMENT,
    IN_PICKUP,
    IN_DROPOFF,
    DELIVERED,
    CANCELLED
}