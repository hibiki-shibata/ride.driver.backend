// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.courier.model

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
import com.ride.driver.backend.shared.model.Coordinate

@Entity
@Table(
    name = "courier_profile",
    indexes = [
        Index(name = "idx_courier_phone_number", columnList = "phone_number"),
        Index(name = "idx_courier_area_id", columnList = "operation_area_id"),
        Index(name = "idx_courier_status", columnList = "cp_status")
    ]
)

class CourierProfile(    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "phone_number", unique = true, nullable = false)
    var phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    var vehicleType: VehicleType,

    @Column(name = "password_hash")
    var passwordHash: String,

    @Column(name = "cp_rate")
    var cpRate: Double? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "cp_status")
    var cpStatus: CourierStatus = CourierStatus.ONBOARDING,
    
    @Column(name = "cp_comments")
    var cpComments: String? = "No comments",

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "current_latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "current_longitude"))
    )
    var currentLocation: Coordinate?,

    // @JoinColumn(name = "area_id")
    @ManyToOne
    @JoinColumn(name = "operation_area_id")
    var operationArea: OperationArea? = null
)