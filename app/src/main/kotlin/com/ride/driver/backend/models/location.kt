package com.ride.driver.backend.models

import jakarta.persistence.Embeddable

@Embeddable
data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)