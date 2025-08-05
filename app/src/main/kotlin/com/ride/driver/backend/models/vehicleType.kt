package com.ride.driver.backend.models


import jakarta.persistence.Entity

@Entity
enum class VehicleType {
    CAR,
    BIKE,
    TRUCK,
    VAN
}