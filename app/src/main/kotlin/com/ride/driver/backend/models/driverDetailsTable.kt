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
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.Index

@Entity
@Table(
    name = "driver_details",
    indexes = [
        Index(name = "idx_driver_phone_number", columnList = "phoneNumber"),
        Index(name = "idx_driver_area_id", columnList = "area_id"),
        Index(name = "idx_driver_status", columnList = "status")
    ]
)

data class DriverDetails(    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    val id: UUID? = null,

    @NotBlank
    val phoneNumber: String,

    @NotBlank
    val name: String,

    @Enumerated(EnumType.STRING)
    val vehicleType: VehicleType,

    @Embedded
    val location: Location,
    val assignId: String,
    val rate: Double,

    @Enumerated(EnumType.STRING)
    val status: DriverStatus = DriverStatus.AVAILABLE,
    
    @ManyToOne
    // @JoinColumn(name = "area_id")
    val area: Area? = null,
    val driverComments: String,

    //  @Version
    // val version: Int = 0
) 

