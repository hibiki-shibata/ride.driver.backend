// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models

import java.util.UUID

import com.ride.driver.backend.models.*
import org.springframework.data.annotation.Id

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.validation.constraints.NotBlank



@Entity
data class DriverDetails(
    
    @Id
    @GeneratedValue(generator = "UUID", strategy = jakarta.persistence.GenerationType.AUTO)
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








