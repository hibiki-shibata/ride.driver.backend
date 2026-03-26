// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.merchant.model

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
import jakarta.persistence.FetchType
import com.ride.driver.backend.shared.model.Coordinate

@Entity
@Table(
    name = "merchant_item",
    indexes = [
        Index(name = "idx_merchant_item_id", columnList = "id"),
        Index(name = "idx_merchant_item_name", columnList = "name"),
        Index(name = "idx_merchant_item_merchant_profile_id", columnList = "merchant_profile")
    ]
)

class MerchantItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    var id: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String,    

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @Column(name = "price", nullable = false)
    var price: Double,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_profile", referencedColumnName = "id", nullable = false)
    val merchantProfile: MerchantProfile
)
