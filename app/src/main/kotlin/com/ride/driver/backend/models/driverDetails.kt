// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models

import java.util.UUID

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.GenerationType


@Entity
data class DriverDetails(
    
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    @Id
    val id: UUID,

    @NotBlank
    val phoneNumber: String,

    @NotBlank
    val name: String,
    val vehicleType: VehicleType,
    val location: Location,
    val assignID: String,
    val rate: Double,

    @NotBlank
    val status: DriverStatus = DriverStatus.AVAILABLE,
    val area: String,
    val driverComments: String = "",
) 




