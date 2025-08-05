package com.ride.driver.backend.models

import jakarta.persistence.Embeddable

@Embeddable
data class Location(
    val latitude: Double,
    val longitude: Double
)