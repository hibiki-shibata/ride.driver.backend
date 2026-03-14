// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.merchant.models

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
import jakarta.persistence.OneToMany
import java.util.List
import com.ride.driver.backend.shared.models.Coordinate

@Entity
@Table(
    name = "merchant_profile",
    indexes = [
        Index(name = "idx_merchant_id", columnList = "id"),
        Index(name = "idx_merchant_name", columnList = "name"),
        Index(name = "idx_merchant_phone_number", columnList = "phone_number"),
        Index(name = "idx_merchant_status", columnList = "merchant_status")
    ]
)

data class MerchantProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "phone_number", unique = true, nullable = false)
    val phoneNumber: String,

    @Column(name = "password_hash")
    val passwordHash: String,

    @Column(name = "mx_address", nullable = false)
    val merchantAddress: String,

    @Column(name = "mx_comments", nullable = true)
    val merchantComments: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "mx_status", nullable = false)
    val merchantStatus: MerchantStatus = MerchantStatus.CLOSED,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "longitude"))
    )
    val merchantAddressCoordiate: Coordinate,

    @OneToMany(mappedBy = "merchantProfile")
    val merchantItems: List<MerchantItem>? = null
)

