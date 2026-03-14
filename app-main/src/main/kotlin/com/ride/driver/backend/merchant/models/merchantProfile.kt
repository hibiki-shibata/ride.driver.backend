// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models.merchantProfile

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
    name = "merchant_profile",
    indexes = [
        Index(name = "idx_merchant_id", columnList = "id"),
        Index(name = "idx_merchant_name", columnList = "merchant_name"),
        Index(name = "idx_merchant_phone_number", columnList = "phone_number"),
        Index(name = "idx_merchant_status", columnList = "merchant_status")
    ]
)

data class MerchantProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "merchant_name", nullable = false)
    val merchantName: String,

    @Column(name = "phone_number", unique = true, nullable = false)
    val phoneNumber: String,

    @Column(name = "merchant_address", nullable = false)
    val merchantAddress: String,

    @Column(name = "merchant_comments", nullable = true)
    val merchantComments: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "merchant_status", nullable = false)
    val merchantStatus: MerchantStatus = MerchantStatus.CLOSED,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "longitude"))
    )
    val merchantAddressCoordiate: Coordinate
)

