// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models

import java.util.UUID

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.GenerationType
import jakarta.persistence.Embedded
import jakarta.persistence.Version


@Entity
class DriverDetails(
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    val id: UUID? = null,

    @NotBlank
    val phoneNumber: String,

    @NotBlank
    val name: String,
    val vehicleType: VehicleType,

    @Embedded
    val location: Location,
    val assignID: String,
    val rate: Double,
    val status: DriverStatus = DriverStatus.AVAILABLE,
    val area: String,
    val driverComments: String,

     @Version
    val version: Int = 0
) 




