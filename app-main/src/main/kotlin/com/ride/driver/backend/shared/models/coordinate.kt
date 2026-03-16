package com.ride.driver.backend.shared.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Coordinate(
    @Column(name = "latitude", unique = false, nullable = false)
    val latitude: Double,
    @Column(name = "longitude", unique = false, nullable = false)
    val longitude: Double
)