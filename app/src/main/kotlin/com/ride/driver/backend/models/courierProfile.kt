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
        Index(name = "idx_courier_phone_number", columnList = "phoneNumber"),
        Index(name = "idx_courier_area_id", columnList = "operationArea_id"),
        Index(name = "idx_courier_status", columnList = "status")
    ]
)

data class CourierProfile(    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    val id: UUID? = null,

    @NotBlank
    val phoneNumber: String,

    @NotBlank
    val name: String,

    @Enumerated(EnumType.STRING)
    val vehicleType: VehicleType,

    val rate: Double,

    @Enumerated(EnumType.STRING)
    val status: CourierStatus = CourierStatus.AVAILABLE,
    
    @ManyToOne
    // @JoinColumn(name = "area_id")
    val operationArea: OperationArea? = null,
    val comments: String,

    //  @Version
    // val version: Int = 0
) 

