package com.ride.driver.backend.models

import jakarta.persistence.Entity

@Entity
enum class DriverStatus {
    AVAILABLE,
    UNAVAILABLE,
    ON_DUTY,
    OFF_DUTY
}