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

// @Entity
// @Table(
//     name = "consumer_profile",
//     indexes = [
        // Index(name = "idx_courier_phone_number", columnList = "phone_number"),
        // Index(name = "idx_courier_area_id", columnList = "operation_area_id"),
        // Index(name = "idx_courier_status", columnList = "cp_status")
//     ]
// )

data class ConsumerProfile(    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "cx_first_name", nullable = false)
    val cxFirstName: String,

    @Column(name = "cx_last_name", nullable = false)
    val cxLastName: String,

    @Column(name = "email_address", unique = true, nullable = false)
    val email_address: String,

    @Column(name = "hash_password", nullable = false)
    val hashPassword: String,
)
    