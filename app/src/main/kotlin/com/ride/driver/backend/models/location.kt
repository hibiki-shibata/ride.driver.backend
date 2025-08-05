package com.ride.driver.backend.models

import jakarta.persistence.Entity


@Entity
class Location(
    val latitude: Double,
    val longitude: Double
)