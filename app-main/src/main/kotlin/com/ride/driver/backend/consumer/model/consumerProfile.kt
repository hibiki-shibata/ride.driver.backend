// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.consumer.model

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
import com.ride.driver.backend.shared.model.Coordinate

@Entity
@Table(
    name = "consumer_profile",
    indexes = [
        // Index(name = "idx_consumer_id", columnList = "id"),
        Index(name = "idx_consumer_email_address", columnList = "email_address"),
    ]
)

class ConsumerProfile(    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "name", nullable = false, length = 50)
    var name: String,

    @Column(name = "email_address", unique = true, nullable = false, length = 50)
    var emailAddress: String,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "longitude"))
    )
    var consumerAddressCoordinate: Coordinate,

    @Column(name = "home_address", nullable = false, length = 100)
    var consumerAddress: String,

    @Column(name = "hash_password", nullable = false, length = 255)
    var passwordHash: String,
)
    