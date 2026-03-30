package com.ride.driver.backend.logistic.model

enum class TaskStatus {
    CREATED,
    READY_FOR_ASSIGNMENT,
    ASSIGNED_TO_COURIER,
    IN_PICKUP,
    IN_DROPOFF,
    DELIVERED,
    CANCELLED
}