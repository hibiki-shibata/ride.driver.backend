// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models.venueProfile

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
    name = "venue_profile",
    indexes = [
        Index(name = "idx_venue_id", columnList = "id"),
        Index(name = "idx_venue_name", columnList = "venue_name"),
        Index(name = "idx_venue_phone_number", columnList = "phone_number"),
        Index(name = "idx_venue_status", columnList = "venue_status")
    ]
)

data class VenueProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "venue_name", nullable = false)
    val venueName: String,

    @Column(name = "phone_number", unique = true, nullable = false)
    val phoneNumber: String,

    @Column(name = "venue_address", nullable = false)
    val venueAddress: String,

    @Column(name = "venue_comments", nullable = true)
    val venueComments: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "venue_status", nullable = false)
    val venueStatus: VenueStatus = VenueStatus.CLOSED,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "longitude"))
    )
    val venueAddressCoordiate: Coordinate
)

