package com.ride.driver.backend.logistic.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.OneToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.Index
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.FetchType
import jakarta.persistence.AttributeOverride
import jakarta.persistence.CascadeType
import java.util.UUID
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.merchant.models.MerchantProfile

@Entity
@Table(
    name = "Task",
    indexes = [
        Index(name = "idx_assigned_courier_id", columnList = "assigned_courier_id"),
        Index(name = "idx_consumer_profile_id", columnList = "consumer_profile"),
        Index(name = "idx_merchant_profile_id", columnList = "merchant_profile"),
        Index(name = "idx_task_status", columnList = "task_status")
    ]
)

data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

    // @Column(name = "assigned_courier_id", nullable = false)
    // val assignedCourierId: UUID? = null,

    @Column(name = "order_time", nullable = false)
    val orderTime: Long = System.currentTimeMillis(),

    @Column(name = "task_note", nullable = true)
    val taskNote: String? = null,

    // @JoinColumn(name = "courier_profile", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_profile", nullable = true, referencedColumnName = "id")
    val courierProfile: CourierProfile? = null,
    
    // @JoinColumn(name = "consumer_profile", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_profile", nullable = false, referencedColumnName = "id")
    val consumerProfile: ConsumerProfile,

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "merchant_profile", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_profile", nullable = false, referencedColumnName = "id")
    val merchantProfile: MerchantProfile,

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    val taskStatus: TaskStatus,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "task_id", nullable = false)
    val orderedItems: List<OrderedItem> = emptyList()
)