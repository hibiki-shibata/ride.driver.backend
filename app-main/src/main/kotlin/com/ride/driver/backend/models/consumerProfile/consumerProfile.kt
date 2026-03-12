// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models.consumerProfile

import java.util.UUID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.persistence.Index
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.AttributeOverride
import com.ride.driver.backend.models.Coordinate

@Entity
@Table(
    name = "consumer_profile",
    indexes = [
        Index(name = "idx_consumer_email_address", columnList = "email_address"),
        Index(name = "idx_consumer__id", columnList = "id"),
    ]
)

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
    val emailAddress: String,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "longitude"))
    )
    val homeAddressCoordinate: Coordinate,

    @Column(name = "home_address", nullable = false)
    val homeAddress: String,
    @Column(name = "hash_password", nullable = false)
    val hashPassword: String,
)
    