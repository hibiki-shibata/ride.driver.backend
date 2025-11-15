// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models

import java.util.UUID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.Index
import jakarta.persistence.Column

@Entity
@Table(
    name = "courier_profile",
    indexes = [
        Index(name = "idx_courier_phone_number", columnList = "phoneNumber"),
        Index(name = "idx_courier_area_id", columnList = "operation_area_id"),
        Index(name = "idx_courier_status", columnList = "status")
    ]
)

data class CourierProfile(    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "phone_number", unique = true, nullable = false)
    val phoneNumber: String,

    @Column(name = "password_hash")
    val passwordHash: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "vehicle_type")
    @Enumerated(EnumType.STRING)
    val vehicleType: VehicleType? = VehicleType.BIKE,

    @Column(name = "rate")
    val rate: Double? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: CourierStatus = CourierStatus.AVAILABLE,
    
    @Column(name = "comments")
    val comments: String? = "No comments",
    
    // @JoinColumn(name = "area_id")
    @ManyToOne
    @JoinColumn(name = "operation_area_id")
    val operationArea: OperationArea? = null
)