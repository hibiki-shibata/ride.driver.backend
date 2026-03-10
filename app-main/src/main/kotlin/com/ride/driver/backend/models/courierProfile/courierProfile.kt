// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models.courierProfile

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
import jakarta.persistence.Embedded
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.AttributeOverride
import com.ride.driver.backend.models.Coordinate

@Entity
@Table(
    name = "courier_profile",
    indexes = [
        Index(name = "idx_courier_phone_number", columnList = "phone_number"),
        Index(name = "idx_courier_area_id", columnList = "operation_area_id"),
        Index(name = "idx_courier_status", columnList = "cp_status")
    ]
)

data class CourierProfile(    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "cp_first_name", nullable = false)
    val cpFirstName: String,

    @Column(name = "cp_last_name", nullable = false)
    val cpLastName: String,

    @Column(name = "phone_number", unique = true, nullable = false)
    val phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    val vehicleType: VehicleType,

    @Column(name = "password_hash")
    val passwordHash: String? = null,

    @Column(name = "cp_rate")
    val cpRate: Double? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "cp_status")
    val cpStatus: CourierStatus = CourierStatus.AVAILABLE,
    
    @Column(name = "cp_comments")
    val cpComments: String? = "No comments",

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "current_latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "current_longitude"))
    )
    val currentLocation: Coordinate,

    // @JoinColumn(name = "area_id")
    @ManyToOne
    @JoinColumn(name = "operation_area_id")
    val operationArea: OperationArea? = null
)