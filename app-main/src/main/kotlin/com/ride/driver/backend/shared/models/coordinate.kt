package com.ride.driver.backend.shared.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Coordinate(
    @Column(name = "latitude", unique = false, nullable = false)
    var latitude: Double,
    @Column(name = "longitude", unique = false, nullable = false)
    var longitude: Double
)