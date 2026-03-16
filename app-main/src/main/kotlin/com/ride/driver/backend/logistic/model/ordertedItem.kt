package com.ride.driver.backend.logistic.model

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
import jakarta.persistence.FetchType

@Entity
@Table(
    name = "ordered_item",
    indexes = [
        Index(name = "idx_ordered_item_id", columnList = "item_id"),
        Index(name = "idx_ordered_item_merchant_item", columnList = "merchant_item")
    ]
)

data class OrderedItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "item_id", nullable = false)
    val itemId: UUID,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "description", nullable = true)
    val description: String? = null,

    @Column(name = "price", nullable = false)
    val price: Double,

    @Column(name = "merchant_id", nullable = false)
    val merchantId: UUID,
)